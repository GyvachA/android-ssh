package com.gyvacha.androidssh.ui.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermission(
    onGrant: () -> Unit
) {
    val latestOnClick by rememberUpdatedState(onGrant)
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            latestOnClick()
        }
    }

    LaunchedEffect(Unit) {
        if (permission == null) {
            latestOnClick()
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                latestOnClick()
            } else {
                launcher.launch(permission)
            }
        }
    }
}

@Composable
fun RequestVpnPermission(
    onGrant: () -> Unit
) {
    val latestOnClick by rememberUpdatedState(onGrant)
    val context = LocalContext.current
    val prepareIntent = remember { VpnService.prepare(context) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            latestOnClick()
        }
    }

    LaunchedEffect(Unit) {
        if (prepareIntent == null) {
            latestOnClick()
        } else {
            launcher.launch(prepareIntent)
        }
    }
}
