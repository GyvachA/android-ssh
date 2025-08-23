package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.EditSshKeyDialog
import com.gyvacha.androidssh.ui.components.GenerateSshKeyDialog
import com.gyvacha.androidssh.ui.components.SettingsCard
import com.gyvacha.androidssh.ui.components.SshKeyCard
import com.gyvacha.androidssh.ui.components.SshKeysBottomSheet
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
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopAppBarWithBackButton(topAppBarParams) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
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

        if (uiState.extendedSshKeys && !uiState.isShowGenerateSshKeyDialog) {
            SshKeysBottomSheet(
                onDismissRequest = { viewModel.updateSshKeyExtended(false) },
                generateSshKeyClick = { viewModel.updateShowGenerateSshKeyDialog(true) },
                addSshKeyClick = { viewModel.updateShowAddSshKeyDialog(true) },
                modifier = Modifier.padding(
                    top = padding.calculateTopPadding()
                )
            ) { sshKey ->
                SshKeyCard(
                    sshKey = sshKey,
                    onClick = {
                        viewModel.updateEditSshKey(sshKey.sshKeyId)
                    },
                    isShowMenu = true,
                    onDeleteSshKey = { viewModel.deleteSshKey(sshKey) }
                )
            }
        }

        if (uiState.isShowGenerateSshKeyDialog) {
            GenerateSshKeyDialog(
                onSave = { viewModel.updateShowGenerateSshKeyDialog(false) },
                onDismiss = { viewModel.updateShowGenerateSshKeyDialog(false) }
            )
        }

        if (uiState.editSshKeyId != null || uiState.isShowAddSshKeyDialog) {
            EditSshKeyDialog(
                onSave = {
                    viewModel.updateEditSshKey(null)
                    viewModel.updateShowAddSshKeyDialog(false)
                },
                onDismiss = {
                    viewModel.updateEditSshKey(null)
                    viewModel.updateShowAddSshKeyDialog(false)
                },
                sshKeyId = uiState.editSshKeyId
            )
        }
    }
}
