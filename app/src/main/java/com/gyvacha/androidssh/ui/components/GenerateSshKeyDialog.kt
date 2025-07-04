package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.utils.SshKeyGenerator

@Composable
fun GenerateSshKeyDialog(
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sshKeyAlias by rememberSaveable { mutableStateOf("Host Key") }
    var sshKeyAlgorithm by rememberSaveable { mutableStateOf(SshKeyGenerator.Algorithm.ALGORITHM_ED25519.title) }
    var sshKeyAlgorithmMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val algorithmList = listOf(
        SshKeyGenerator.Algorithm.ALGORITHM_ED25519.title,
        SshKeyGenerator.Algorithm.ALGORITHM_RSA.title
    )
    var saveButtonEnabled by rememberSaveable { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(R.string.create_ssh_key)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextFieldCharacterCount(
                    label = { Text(stringResource(R.string.alias)) },
                    value = sshKeyAlias,
                    onValueChange = {
                        saveButtonEnabled = it.isNotBlank()
                        sshKeyAlias = it
                    },
                    maxLength = 30,
                    isError = sshKeyAlias.isBlank(),
                    errorMessage = if (sshKeyAlias.isBlank()) stringResource(R.string.string_blank_error) else null
                )
                DropdownMenuBase(
                    selectedOption = sshKeyAlgorithm,
                    onDismiss = { sshKeyAlgorithmMenuExpanded = false },
                    expanded = sshKeyAlgorithmMenuExpanded,
                    onMenuClick = { sshKeyAlgorithmMenuExpanded = true },
                    label = stringResource(R.string.algorithm),
                ) {
                    algorithmList.forEach { algorithm ->
                        DropdownMenuItem(
                            text = { Text(algorithm) },
                            onClick = {
                                sshKeyAlgorithm = algorithm
                                sshKeyAlgorithmMenuExpanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(sshKeyAlias, sshKeyAlgorithm)
                },
                enabled = saveButtonEnabled
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

@Composable
@Preview
fun GenerateSshKeyDialogPreview() {
    GenerateSshKeyDialog(
        onSave = { _, _ -> },
        onDismiss = {},
    )
}
