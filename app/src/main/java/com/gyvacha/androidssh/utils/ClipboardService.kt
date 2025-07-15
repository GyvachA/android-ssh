package com.gyvacha.androidssh.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

class ClipboardService(
    private val clipboard: Clipboard
) {
    suspend fun getText(): String? {
        val clip = clipboard.getClipEntry()
        if (clip != null && clip.clipData.itemCount > 0) {
            val item = clip.clipData.getItemAt(0)
            return item.text?.toString()
        }
        return null
    }

    suspend fun setText(label: String, value: String) {
        val clip = ClipEntry(ClipData.newPlainText(label, value))
        clipboard.setClipEntry(clip)
    }
}