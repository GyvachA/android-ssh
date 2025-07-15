package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
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
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.getAddress

@Composable
fun SingboxConfigCard(
    config: ProxyConfig,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteConfig: ((ProxyConfig) -> Unit)? = null,
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }

    BaseCard(
        modifier = modifier,
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconWithBackground(Icons.Filled.Book)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = dimensionResource(R.dimen.medium_padding))
            ) {
                Text(
                    text = config.alias,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = config.getAddress(),
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
                Text(
                    text = "${config.type}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }

            MenuWithIcon(
                expanded = expandedMenu,
                onDismiss = { expandedMenu = false },
                onMenuClick = { expandedMenu = true },
                content = {
                    if (onDeleteConfig != null) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete)) },
                            onClick = {
                                onDeleteConfig(config)
                            }
                        )
                    }
                }
            )

        }
    }
}

@Preview
@Composable
fun SingboxConfigCardPreview() {
    SingboxConfigCard(
        config = ProxyConfig(
            0,
            "Alias",
            ProxyType.VLESS,
            ProxySpec.Vless("123.0.0.1", 22, "uuid"),
            isActive = false
        ),
        onCardClick = {}
    )
}