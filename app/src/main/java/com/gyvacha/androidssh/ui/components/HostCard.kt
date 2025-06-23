package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.Host

@Composable
fun HostCard(host: Host, modifier: Modifier = Modifier) {
    BaseCard(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.medium_padding)),
        ) {
            IconWithBackground(Icons.Filled.Dns)
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.medium_padding)),
                verticalArrangement = Arrangement.Center
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
        }
    }
}
