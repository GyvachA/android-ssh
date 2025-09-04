package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.ui.utils.GenerateSshKeyViewEvent
import com.gyvacha.androidssh.ui.viewmodel.GenerateSshKeyViewModel
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.SshKeyGenerator

@Composable
fun GenerateSshKeyDialog(
    onSave: (SshKey) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GenerateSshKeyViewModel = hiltViewModel()
) {
    val algorithmList = listOf(
        SshKeyGenerator.Companion.Algorithm.ALGORITHM_ED25519.title,
        SshKeyGenerator.Companion.Algorithm.ALGORITHM_RSA.title
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val messageNotifier = LocalMessageNotifier.current
    val messageSshKeyCreated = stringResource(R.string.ssh_key_created)
    val messageSshKeyCreateError = stringResource(R.string.ssh_key_create_failure)

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                GenerateSshKeyViewEvent.SshKeyCreateFailure -> messageNotifier?.showSnackbar(
                    messageSshKeyCreateError
                )

                GenerateSshKeyViewEvent.SshKeyCreated -> messageNotifier?.showSnackbar(
                    messageSshKeyCreated
                )
            }
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
                    enabled = !uiState.isLoading,
                    label = { Text(stringResource(R.string.alias)) },
                    value = uiState.sshKeyAlias,
                    onValueChange = {
                        viewModel.updateSaveButtonEnabled(it.isNotBlank())
                        viewModel.updateSshKeyAlias(it)
                    },
                    maxLength = 30,
                    isError = uiState.sshKeyAlias.isBlank(),
                    errorMessage = if (uiState.sshKeyAlias.isBlank()) {
                        stringResource(R.string.string_blank_error)
                    } else {
                        null
                    }
                )
                DropdownMenuBase(
                    enabled = !uiState.isLoading,
                    selectedOption = uiState.sshKeyAlgorithm,
                    onDismiss = { viewModel.updateSshKeyAlgorithmMenuExpanded(false) },
                    expanded = uiState.sshKeyAlgorithmMenuExpanded,
                    onMenuClick = { viewModel.updateSshKeyAlgorithmMenuExpanded(true) },
                    label = stringResource(R.string.algorithm),
                ) {
                    algorithmList.forEach { algorithm ->
                        DropdownMenuItem(
                            text = { Text(algorithm) },
                            onClick = {
                                viewModel.updateSshKeyAlgorithm(algorithm)
                                viewModel.updateSshKeyAlgorithmMenuExpanded(false)
                            }
                        )
                    }
                }
                SecureTextField(
                    enabled = !uiState.isLoading,
                    value = uiState.sshKeyPassphrase,
                    onValueChange = viewModel::updateSshKeyPassphrase,
                    label = stringResource(R.string.password),
                    onVisibilityClick = { viewModel.updatePassphraseVisible(!uiState.isPassphraseVisible) },
                    isPasswordVisible = uiState.isPassphraseVisible
                )
            }
        },
        confirmButton = {
            if (!uiState.isLoading) {
                TextButton(
                    onClick = {
                        viewModel.generateSshKey {
                            onSave(it)
                        }
                    },
                    enabled = uiState.saveButtonEnabled
                ) {
                    Text(stringResource(R.string.save))
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.large_padding))
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        modifier = modifier
    )
}

@Composable
@Preview
private fun GenerateSshKeyDialogPreview() {
    GenerateSshKeyDialog(
        onSave = {},
        onDismiss = {},
    )
}
