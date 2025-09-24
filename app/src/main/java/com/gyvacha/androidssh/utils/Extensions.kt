package com.gyvacha.androidssh.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.nekohasekai.libbox.StringIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun StringIterator.toList(): List<String> {
    return mutableListOf<String>().apply {
        while (hasNext()) {
            add(next())
        }
    }
}

fun <T> Flow<T>.throttleFirst(windowMillis: Long): Flow<T> = flow {
    var lastTime = 0L
    collect { value ->
        val now = System.currentTimeMillis()
        if (now - lastTime >= windowMillis) {
            lastTime = now
            emit(value)
        }
    }
}

@Composable
fun Modifier.applySystemBarsPadding(): Modifier =
    this.padding(WindowInsets.systemBars.asPaddingValues())

@Composable
fun Modifier.applyNavBarPadding(): Modifier =
    this.windowInsetsPadding(WindowInsets.navigationBars)
