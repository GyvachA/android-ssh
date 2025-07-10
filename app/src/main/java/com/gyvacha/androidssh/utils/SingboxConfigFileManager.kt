package com.gyvacha.androidssh.utils

import android.content.Context
import java.io.File

class SingboxConfigFileManager(private val context: Context) {
    fun writeToFile(json: String): File {
        val file = File(context.filesDir, "singbox_config.json")
        file.writeText(json)
        return file
    }

    fun getFile(): File = File(context.filesDir, "singbox_config.json")
}