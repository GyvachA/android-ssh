package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.TerminalTextInput
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.TerminalViewModel

@Composable
fun TerminalScreen(
    hostId: Int,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val outputListState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initSshConnect(hostId)
    }
    LaunchedEffect(uiState.terminalOutput.size) {
        val lastIndex = uiState.terminalOutput.lastIndex
        if (lastIndex > -1) outputListState.animateScrollToItem(uiState.terminalOutput.lastIndex)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            TerminalTextInput(
                uiState.terminalInput,
                onValueChange = viewModel::updateTerminalInput,
                onSend = viewModel::sendCommand
            )
        },
        topBar = {
            TopAppBarWithBackButton(topAppBarParams)
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.medium_padding))
            ) {
                LazyColumn(
                    state = outputListState
                ) {
                    items(uiState.terminalOutput) { output ->
                        Text(
                            text = output,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun TerminalScreenPreview() {
    TerminalScreen(0, TopAppBarParams.PREVIEW)
}