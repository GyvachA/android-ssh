package com.gyvacha.androidssh.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R

@Composable
fun PermissionDialog(
    explanationText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.permission_required)) },
        text = { Text(text = explanationText) },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(
                        ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}
