package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.ui.viewmodel.SshKeysViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SshKeysBottomSheet(
    onDismissRequest: () -> Unit,
    generateSshKeyClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SshKeysViewModel = hiltViewModel(),
    addSshKeyClick: (() -> Unit)? = null,
    sshKeyCard: @Composable (SshKey) -> Unit
) {
    val sshKeys by viewModel.sshKeys.collectAsStateWithLifecycle()
    val sshKeysSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sshKeysSheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.medium_padding))
        ) {
            SshKeyOptionCard(
                title = stringResource(R.string.create_ssh_key),
                onClick = generateSshKeyClick
            )
            if (addSshKeyClick != null) {
                SshKeyOptionCard(
                    title = stringResource(R.string.add_ssh_key),
                    onClick = addSshKeyClick
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
            ) {
                items(sshKeys) { sshKey ->
                    sshKeyCard(sshKey)
                }
            }
        }
    }
}
