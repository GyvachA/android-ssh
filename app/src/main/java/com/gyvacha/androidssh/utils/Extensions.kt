package com.gyvacha.androidssh.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

inline fun SharedPreferences.edit(operation: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.operation()
    editor.apply()
}

fun String.toLibSuffix(): String = when (this) {
    "arm64-v8a" -> "arm64"
    "armeabi-v7a" -> "arm"
    "x86", "x86_64" -> "386"
    else -> error("Unsupported ABI: $this")
}

