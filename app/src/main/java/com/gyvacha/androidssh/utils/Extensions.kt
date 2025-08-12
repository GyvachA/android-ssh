package com.gyvacha.androidssh.utils

import io.nekohasekai.libbox.StringIterator

fun StringIterator.toList(): List<String> {
    return mutableListOf<String>().apply {
        while (hasNext()) {
            add(next())
        }
    }
}
