package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.DropdownMenuItem
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
import com.gyvacha.androidssh.R

@Composable
fun KnownHostCard(
    host: String,
    fingerprint: String,
    onDeleteKnownHost: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }

    BaseCard(
        modifier = modifier,
        onClick = {}
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
                    text = host,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Fingerprint: $fingerprint",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            MenuWithIcon(
                expanded = expandedMenu,
                onDismiss = { expandedMenu = false },
                onMenuClick = { expandedMenu = true },
                content = {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            onDeleteKnownHost()
                            expandedMenu = false
                        }
                    )
                }
            )
        }
    }
}
