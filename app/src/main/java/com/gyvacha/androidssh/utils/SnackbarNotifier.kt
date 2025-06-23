package com.gyvacha.androidssh.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SnackbarNotifier(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope
) {

    fun showSnackbar(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = duration,
            )
        }
    }
}

val LocalMessageNotifier = staticCompositionLocalOf<SnackbarNotifier?> { null }
