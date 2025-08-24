package com.gyvacha.androidssh.utils

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
