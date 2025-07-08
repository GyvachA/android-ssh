package com.gyvacha.androidssh

import android.app.Application
import com.google.crypto.tink.aead.AeadConfig
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class SshApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        AeadConfig.register()
        System.loadLibrary("sqlcipher")
    }
}