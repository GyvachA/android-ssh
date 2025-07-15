package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.utils.ClipboardService
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import kotlinx.coroutines.launch

@Composable
fun SshKeyCard(
    onClick: () -> Unit,
    sshKey: SshKey,
    modifier: Modifier = Modifier,
    actionButtonImage: ImageVector,
    actionButtonDesc: String?,
    isShowMenu: Boolean = false,
    onDeleteSshKey: ((SshKey) -> Unit)? = null
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }
    val clipboardService = ClipboardService(LocalClipboard.current)
    val snackbarNotifier = LocalMessageNotifier.current
    val clipboardScope = rememberCoroutineScope()
    val keyCopiedText = stringResource(R.string.key_copied)

    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconWithBackground(Icons.Filled.Key)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = dimensionResource(R.dimen.medium_padding))
            ) {
                Text(
                    text = sshKey.alias,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "pub key: ${sshKey.publicKey.take(20)}...",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }

            if (isShowMenu) {
                MenuWithIcon(
                    expanded = expandedMenu,
                    onDismiss = { expandedMenu = false },
                    onMenuClick = { expandedMenu = true },
                    content = {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy_public_key)) },
                            onClick = {
                                clipboardScope.launch {
                                    clipboardService.setText("SSH key", sshKey.publicKey)
                                    snackbarNotifier?.showSnackbar(keyCopiedText)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.copy_private_key)) },
                            onClick = {
                                clipboardScope.launch {
                                    clipboardService.setText("SSH key", sshKey.privateKey)
                                    snackbarNotifier?.showSnackbar(keyCopiedText)
                                }
                            }
                        )
                        if (onDeleteSshKey != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete)) },
                                onClick = {
                                    onDeleteSshKey(sshKey)
                                }
                            )
                        }
                    }
                )
            }
            IconButton(
                onClick = onClick
            ) {
                Icon(imageVector = actionButtonImage, contentDescription = actionButtonDesc)
            }
        }
    }
}

@Composable
@Preview
fun SshKeyCardPreview() {
    SshKeyCard(
        sshKey = SshKey(
            alias = "Preview",
            publicKey = "rsa-pub asdasdasdsadasd",
            privateKey = "priv"
        ),
        onClick = {},
        actionButtonImage = Icons.Filled.Add,
        actionButtonDesc = null
    )
}
