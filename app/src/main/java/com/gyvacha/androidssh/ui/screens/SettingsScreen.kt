package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.BaseCard
import com.gyvacha.androidssh.ui.components.SettingsCard
import com.gyvacha.androidssh.ui.components.SshKeyCard
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sshKeysSheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopAppBarWithBackButton(topAppBarParams) }
    ) { padding ->
        Column(
            modifier = modifier.padding(padding)
                .padding(dimensionResource(R.dimen.medium_padding))
                .verticalScroll(scrollState)
        ) {
            SettingsCard(
                title = stringResource(R.string.ssh_keys),
                onCardClick = {
                    viewModel.updateSshKeyExtended(true)
                }
            )
        }

        if (uiState.extendedSshKeys) {
            val sshKeys by viewModel.sshKeys.collectAsStateWithLifecycle()

            ModalBottomSheet(
                sheetState = sshKeysSheetState,
                onDismissRequest = { viewModel.updateSshKeyExtended(false) },
                modifier = modifier.padding(
                    top = padding.calculateTopPadding(),
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    BaseCard(
                        onClick = {

                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.create_ssh_key),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.updateSshKeyExtended(true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.create_ssh_key)
                                )
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        items(sshKeys) { sshKey ->
                            SshKeyCard(
                                sshKey = sshKey,
                                onClick = {

                                },
                                actionButtonImage = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                actionButtonDesc = stringResource(R.string.choose_ssh_key)
                            )
                        }
                    }
                }
            }
        }
    }
}