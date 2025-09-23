package com.gyvacha.androidssh.utils

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.io.File
import java.nio.charset.StandardCharsets

class SingboxConfigFileManager(context: Context) {

    private val configFile = File(context.filesDir, "singbox_config.json")
    private val aead: Aead

    init {
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, DatabaseKeyManager.KEYSET_NAME, DatabaseKeyManager.KEYSET_PREF)
            .withMasterKeyUri(DatabaseKeyManager.MASTER_KEY_URI)
            .build()
            .keysetHandle
        aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
    }

    fun writeToFile(json: String) {
        val encrypted = aead.encrypt(json.toByteArray(StandardCharsets.UTF_8), null)
        val encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        configFile.writeText(encoded)
    }

    fun readFromFile(): String? {
        if (!configFile.exists()) return null
        val decoded = Base64.decode(configFile.readText(), Base64.NO_WRAP)
        val decrypted = aead.decrypt(decoded, null)
        return String(decrypted, StandardCharsets.UTF_8)
    }
}
