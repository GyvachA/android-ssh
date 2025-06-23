package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R

@Composable
fun BottomFabSaveActions(
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaveButtonActive: Boolean = true,
) {
    Box(
        modifier = modifier
            .padding(
                bottom = dimensionResource(R.dimen.medium_padding),
                start = dimensionResource(R.dimen.medium_padding),
                end = dimensionResource(R.dimen.medium_padding),
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCancel,
                content = { Text(text = stringResource(R.string.cancel)) }
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            Button(
                modifier = Modifier.weight(1f),
                onClick = onSave,
                enabled = isSaveButtonActive,
                content = { Text(text = stringResource(R.string.save)) }
            )
        }
    }
}