package com.gyvacha.androidssh

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.google.crypto.tink.aead.AeadConfig
import com.gyvacha.androidssh.service.SingboxService
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.Security

@HiltAndroidApp
class SshApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val nativeLibDir = applicationInfo.nativeLibraryDir
        Log.d("NativeLibs", "Native libs dir: $nativeLibDir")
        val files = File(nativeLibDir).listFiles()
        Log.d("NativeLibs", "Native libs: ${files?.map { it.name }}")
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        AeadConfig.register()
        System.loadLibrary("sqlcipher")
        System.loadLibrary("native-lib")

        val channel = NotificationChannel(
            SingboxService.NOTIFICATION_CHANNEL,
            "Singbox Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}