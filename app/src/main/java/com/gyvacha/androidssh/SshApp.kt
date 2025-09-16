package com.gyvacha.androidssh

import android.app.Application
import android.util.Log
import com.google.crypto.tink.aead.AeadConfig
import com.yandex.mobile.ads.common.MobileAds
import dagger.hilt.android.HiltAndroidApp
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.SetupOptions
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.Security

@HiltAndroidApp
class SshApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        AeadConfig.register()
        System.loadLibrary("sqlcipher")

        // initialize libbox
        val baseDir = filesDir
        baseDir.mkdirs()
        val workingDir = getExternalFilesDir(null) ?: return
        workingDir.mkdirs()
        val tempDir = cacheDir
        tempDir.mkdirs()
        Libbox.setup(
            SetupOptions().also {
                it.basePath = baseDir.path
                it.workingPath = workingDir.path
                it.tempPath = tempDir.path
            }
        )
        Libbox.redirectStderr(File(workingDir, "stderr.log").path)

        // Yandex mobileads
        MobileAds.initialize(
            context = this,
            initializationListener = {
                Log.d("YandexAds", "Initialized")
            }
        )
    }
}
