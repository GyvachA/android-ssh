package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R

@Composable
fun KebabMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Box(modifier = modifier) {
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.open_menu)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onDismiss() },
            modifier = Modifier,
            content = content
        )
    }
}