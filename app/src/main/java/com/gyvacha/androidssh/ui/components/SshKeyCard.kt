package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshKey

@Composable
fun SshKeyCard(
    onClick: () -> Unit,
    sshKey: SshKey,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row (
            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconWithBackground(Icons.Filled.Key)
            Text(
                text = sshKey.alias,
                modifier = Modifier.weight(1f)
                    .padding(start = dimensionResource(R.dimen.medium_padding))
            )
            IconButton(
                onClick = onClick
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.select_ssh_key))
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
            publicKey = "pub",
            privateKey = "priv"
        ),
        onClick = {}
    )
}
