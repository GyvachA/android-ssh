package com.gyvacha.androidssh.utils

import android.content.Context
import androidx.core.content.edit
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.security.SecureRandom

object DatabaseKeyManager {

    private const val MASTER_KEY_URI = "android-keystore://db_master_key"
    private const val KEYSET_PREF = "db_encrypted_prefs"
    private const val KEYSET_NAME = "encrypted_passphrase_key"
    private const val PREF_NAME = "host_db"
    private const val PREF_DATABASE_PASSPHRASE = "encrypted_db_passphrase"

    fun getOrCreateSecretKey(context: Context): ByteArray {
        val aead = getOrCreateAead(context)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val encrypted = prefs.getString(PREF_DATABASE_PASSPHRASE, null)

        return if (encrypted != null) {
            aead.decrypt(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT), null)
        } else {
            val passphrase = ByteArray(32).also {
                SecureRandom().nextBytes(it)
            }
            val ciphertext = aead.encrypt(passphrase, null)
            prefs.edit {
                putString(
                    PREF_DATABASE_PASSPHRASE,
                    android.util.Base64.encodeToString(ciphertext, android.util.Base64.DEFAULT)
                )
            }
            passphrase
        }
    }

    private fun getOrCreateAead(context: Context): Aead {
        val handle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, KEYSET_PREF)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        return handle.getPrimitive(Aead::class.java)
    }
}