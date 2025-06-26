package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.Host

@Composable
fun HostCard(
    host: Host,
    modifier: Modifier = Modifier,
    onStartTerminal: () -> Unit,
) {
    BaseCard(
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
                    style = MaterialTheme.typography.labelMedium,
                    text = "${host.hostNameOrIp}:${host.port}, ${host.userName}"
                )
            }
            IconButton(
                onClick = onStartTerminal
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.start_terminal))
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
            userName = "UserName"
        ),
        onStartTerminal = {}
    )
}
