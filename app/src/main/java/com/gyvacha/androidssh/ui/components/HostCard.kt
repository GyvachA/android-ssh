package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.SshAuthType

@Composable
fun HostCard(
    host: Host,
    modifier: Modifier = Modifier,
    onStartTerminal: () -> Unit,
    onCardClick: () -> Unit,
    onEditHost: ((Host) -> Unit)? = null,
    onDeleteHost: ((Host) -> Unit)? = null,
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }

    BaseCard(
        onClick = { onCardClick() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconWithBackground(Icons.Filled.Dns)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.medium_padding)),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    host.alias,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    text = "${host.hostNameOrIp}:${host.port}, ${host.userName}"
                )
            }
            if (onEditHost != null || onDeleteHost != null) {
                KebabMenu(
                    expanded = expandedMenu,
                    onDismiss = { expandedMenu = false },
                    onMenuClick = { expandedMenu = true },
                    content = {
                        if (onEditHost != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit)) },
                                onClick = {
                                    onEditHost(host)
                                }
                            )
                        }
                        if (onDeleteHost != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete)) },
                                onClick = {
                                    onDeleteHost(host)
                                }
                            )
                        }
                    }
                )
            }
            IconButton(
                onClick = onStartTerminal
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.start_terminal)
                )
            }
        }
    }
}

@Composable
@Preview
fun HostCardPreview() {
    HostCard(
        Host(
            hostId = 0,
            alias = "Alias",
            hostNameOrIp = "127.0.0.1",
            port = 22,
            userName = "UserName",
            authType = SshAuthType.SSH_KEY
        ),
        onStartTerminal = {},
        onCardClick = {}
    )
}
