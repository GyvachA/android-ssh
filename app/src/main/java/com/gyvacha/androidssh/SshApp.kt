package com.gyvacha.androidssh

import android.app.Application
import android.util.Log
import com.google.crypto.tink.aead.AeadConfig
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
        val nativeLibDir = applicationInfo.nativeLibraryDir
        Log.d("NativeLibs", "Native libs dir: $nativeLibDir")
        val files = File(nativeLibDir).listFiles()
        Log.d("NativeLibs", "Native libs: ${files?.map { it.name }}")
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
    }
}
