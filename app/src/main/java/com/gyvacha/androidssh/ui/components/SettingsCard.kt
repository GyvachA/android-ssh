package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.gyvacha.androidssh.R

@Composable
fun SettingsCard(
    title: String,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.large_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = dimensionResource(R.dimen.medium_padding)),
                imageVector = Icons.Filled.Key,
                contentDescription = title
            )
            Text(title)
        }
    }
}

@Preview
@Composable
fun SettingsCardPreview() {
    SettingsCard(
        title = "Settings",
        onCardClick = {}
    )
}