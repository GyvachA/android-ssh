package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.TerminalTextInput
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.TerminalViewModel

@Composable
fun TerminalScreen(
    navController: NavController,
    hostId: Int,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: TerminalViewModel = hiltViewModel(),

) {
    val outputScrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initSshConnect(hostId)
    }
    LaunchedEffect(uiState.terminalOutput) {
        outputScrollState.animateScrollTo(outputScrollState.maxValue)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            TerminalTextInput(
                uiState.terminalInput,
                onValueChange = viewModel::updateTerminalInput,
                onSend = viewModel::sendCommand,
                enabled = !uiState.isLoading,
            )
        },
        topBar = {
            TopAppBarWithBackButton(topAppBarParams)
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(dimensionResource(R.dimen.medium_padding))
                    .verticalScroll(outputScrollState)
            ) {
                Text(
                    text = uiState.terminalOutput,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                )
            }
        }
    }
    uiState.pendingHostKey?.let { fingerprint ->
        AlertDialog(
            onDismissRequest = {
                navController.navigateUp()
            },
            title = { Text(stringResource(R.string.new_host_detected)) },
            text = { Text("Fingerprint: $fingerprint") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.confirmHostKey(true)
                }) {
                    Text(stringResource(R.string.trust))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.confirmHostKey(false)
                    navController.navigateUp()
                }) {
                    Text(stringResource(R.string.reject))
                }
            }
        )
    }
}

@Composable
@Preview
private fun TerminalScreenPreview() {
    TerminalScreen(rememberNavController(), 0, TopAppBarParams.PREVIEW)
}
