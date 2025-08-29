package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.ui.viewmodel.EditSshKeyViewModel

@Composable
fun EditSshKeyDialog(
    onSave: (SshKey) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditSshKeyViewModel = hiltViewModel(),
    sshKeyId: Int? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (sshKeyId != null) {
            viewModel.getSshKey(sshKeyId)
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.create_ssh_key)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldCharacterCount(
                    label = { Text(stringResource(R.string.alias) + "*") },
                    value = uiState.sshKey.alias,
                    onValueChange = {
                        viewModel.updateAlias(it)
                    },
                    maxLength = 30,
                    isError = uiState.sshKey.alias.isBlank(),
                    errorMessage = if (uiState.sshKey.alias.isBlank()) {
                        stringResource(R.string.string_blank_error)
                    } else {
                        null
                    }
                )
                TextFieldBase(
                    label = { Text(stringResource(R.string.public_key) + "*") },
                    value = uiState.sshKey.publicKey,
                    onValueChange = {
                        viewModel.updatePublicKey(it)
                    },
                    isError = uiState.sshKey.publicKey.isBlank(),
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.sshKey.publicKey.isBlank()) {
                                Text(text = stringResource(R.string.string_blank_error))
                            }
                        }
                    }
                )
                SecureTextField(
                    value = uiState.sshKey.privateKey,
                    onValueChange = viewModel::updatePrivateKey,
                    label = stringResource(R.string.private_key) + "*",
                    onVisibilityClick = { viewModel.updatePrivateKeyVisible(!uiState.isPrivateKeyVisible) },
                    isPasswordVisible = uiState.isPrivateKeyVisible,
                    isError = uiState.sshKey.privateKey.isBlank(),
                    errorMessage = stringResource(R.string.string_blank_error)
                )
                SecureTextField(
                    value = uiState.sshKey.passphrase ?: "",
                    onValueChange = viewModel::updatePassphrase,
                    label = stringResource(R.string.password),
                    onVisibilityClick = { viewModel.updatePassphraseVisible(!uiState.isPassphraseVisible) },
                    isPasswordVisible = uiState.isPassphraseVisible
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sshKeyId != null) {
                        viewModel.updateSshKeyLocal()
                    } else {
                        viewModel.insertSshKey()
                    }
                    onSave(uiState.sshKey)
                },
                enabled = uiState.isSaveButtonEnabled
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = modifier
    )
}
