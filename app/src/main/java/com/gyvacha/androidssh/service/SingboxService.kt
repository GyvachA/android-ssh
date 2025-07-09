package com.gyvacha.androidssh.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class SingboxService : Service() {

    companion object {
        const val EXTRA_CONFIG_PATH = "config_path"
        const val EXTRA_LOG_LINE = "log_line"
        const val ACTION_LOG = "singbox_log_broadcast"
        const val NOTIFICATION_CHANNEL = "singbox_channel"
        const val NOTIFICATION_ID = 1
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var process: Process? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configPath = intent?.getStringExtra(EXTRA_CONFIG_PATH) ?: return START_NOT_STICKY
        startForeground(NOTIFICATION_ID, createNotification("Singbox started"))
        scope.launch {
            runSingbox(configPath)
        }
        return START_STICKY
    }

    private suspend fun runSingbox(configPath: String) = withContext(Dispatchers.IO) {
        try {
            val binName = when {
                Build.SUPPORTED_ABIS.any { it.contains("arm64") } -> "sing-box-arm64"
                Build.SUPPORTED_ABIS.any { it.contains("armeabi") } -> "sing-box-arm"
                Build.SUPPORTED_ABIS.any { it.contains("x86_64") } -> "sing-box-386"
                else -> throw IllegalStateException("Unsupported ABI")
            }

            val binFile = File(filesDir, binName)
            if (!binFile.exists()) {
                assets.open(binName).use { input ->
                    binFile.outputStream().use { output -> input.copyTo(output) }
                }
                binFile.setExecutable(true)
            }

            val processBuilder = ProcessBuilder(binFile.absolutePath, "-c", configPath)
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process!!.inputStream))
            reader.forEachLine { sendLog(it) }
            process?.waitFor()
        } catch (e: Exception) {
            sendLog("Ошибка: ${e.message}")
        } finally {
            stopSelf()
        }
    }

    private fun sendLog(log: String) {
        sendBroadcast(Intent(ACTION_LOG).apply {
            putExtra(EXTRA_LOG_LINE, log)
        })
    }

    private fun createNotification(text: String): Notification {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL, "Singbox", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle("Singbox")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        process?.destroy()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}