package com.gyvacha.androidssh.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gyvacha.androidssh.receiver.SingboxActionReceiver
import com.gyvacha.androidssh.utils.SingboxNative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SingboxService : VpnService() {

    companion object {
        const val EXTRA_CONFIG_PATH = "config_path"
        const val EXTRA_LOG_LINE = "log_line"
        const val ACTION_LOG = "singbox_log_broadcast"
        const val NOTIFICATION_CHANNEL = "singbox_channel"
        const val NOTIFICATION_ID = 1001
        const val VPN_IP = "10.0.0.2"
        const val VPN_PREFIX = 32
        const val ACTION_STOP = "singbox_stop"
        const val ACTION_RESTART = "singbox_restart"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> get() = _isRunning
    }

    private var tunInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("SingboxService", "onStartCommand: starting foreground service")

        when (intent?.action) {
            ACTION_STOP -> {
                scope.launch {
                    stopService()
                }
                return START_NOT_STICKY
            }
            ACTION_RESTART -> {
                scope.launch {
                    restartService(intent)
                }
                return START_STICKY
            }
        }

        val configPath = intent?.getStringExtra(EXTRA_CONFIG_PATH)
        if (configPath.isNullOrBlank()) {
            Log.e("SingboxService", "No config path provided")
            stopSelf()
            return START_NOT_STICKY
        }

        val configFile = File(configPath)
        if (!configFile.exists() || !configFile.canRead()) {
            Log.e("SingboxService", "Config file not accessible: $configPath")
            stopSelf()
            return START_NOT_STICKY
        }
        val config = configFile.readText()
        Log.d("SingboxConfig", config)

        createNotificationChannel()

        val tun = createVpnInterface()
        if (tun == null) {
            Log.e("SingboxService", "Failed to establish VPN interface")
            stopSelf()
            return START_NOT_STICKY
        }

        tunInterface?.close()
        tunInterface = tun

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification("Singbox is running", intent),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification("Singbox is running", intent))
            }
        } catch (e: Exception) {
            Log.e("SingboxService", "Failed to start foreground", e)
            stopSelf()
            return START_NOT_STICKY
        }

        scope.launch {
            try {
                _isRunning.emit(true)
                val success = startSingbox(configFile.readText().replace("fd://0", "fd://${tun.fd}"), tun.fd)
                Log.d("SingboxService", "startSingbox success=$success")
                if (!success) {
                    withContext(Dispatchers.Main) { stopSelf() }
                    scope.launch {
                        stopService()
                    }
                }
            } catch (e: Exception) {
                Log.e("SingboxService", "Exception in startSingbox coroutine", e)
                scope.launch {
                    stopService()
                }
            }
        }

        return START_STICKY
    }

    private suspend fun restartService(intent: Intent) {
        _isRunning.emit(false)

        try {
            SingboxNative.stop()
        } catch (e: Exception) {
            Log.e("SingboxService", "Error stopping Singbox during restart", e)
        }

        tunInterface?.close()
        tunInterface = null

        val configPath = intent.getStringExtra(EXTRA_CONFIG_PATH)
        if (configPath.isNullOrBlank()) {
            Log.e("SingboxService", "Restart failed: no config path")
            withContext(Dispatchers.Main) { stopSelf() }
            return
        }

        val configFile = File(configPath)
        if (!configFile.exists() || !configFile.canRead()) {
            Log.e("SingboxService", "Restart failed: config file not accessible")
            withContext(Dispatchers.Main) { stopSelf() }
            return
        }

        val newTun = createVpnInterface()
        if (newTun == null) {
            Log.e("SingboxService", "Failed to establish VPN interface during restart")
            withContext(Dispatchers.Main) { stopSelf() }
            return
        }

        tunInterface = newTun

        withContext(Dispatchers.Main) {
            try {
                val notification = createNotification("Singbox restarted", intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            } catch (e: Exception) {
                Log.e("SingboxService", "Failed to update foreground notification", e)
            }
        }

        val success = startSingbox(configPath, newTun.fd)
        if (!success) {
            scope.launch {
                stopService()
            }
            return
        }

        _isRunning.emit(true)
    }

    private suspend fun startSingbox(configPath: String, fd: Int): Boolean = withContext(Dispatchers.IO) {
        Log.d("SingboxService", "startSingbox called with configPath=$configPath fd=$fd")
        try {
            val result = SingboxNative.start(configPath, fd)
            Log.d("SingboxService", "SingboxNative.startWithConfigPathAndFd result=$result")
            if (result != 0) {
                sendLog("Singbox start error: code $result")
                return@withContext false
            }
            return@withContext true
        } catch (e: Exception) {
            sendLog("Ошибка: ${e.message}")
            Log.e("SingboxService", "Ошибка запуска", e)
            return@withContext false
        }
    }

    private suspend fun stopService() {
        try {
            SingboxNative.stop()
        } catch (e: Exception) {
            Log.e("SingboxService", "Error stopping SingboxNative", e)
        }

        try {
            tunInterface?.close()
            tunInterface = null
        } catch (e: Exception) {
            Log.e("SingboxService", "Error closing VPN interface", e)
        }

        withContext(Dispatchers.Main) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        _isRunning.emit(false)
    }

    private fun sendLog(log: String) {
        mainScope.launch {
            try {
                sendBroadcast(Intent(ACTION_LOG).apply {
                    putExtra(EXTRA_LOG_LINE, log)
                })
            } catch (e: Exception) {
                Log.e("SingboxService", "Failed to send broadcast", e)
            }
        }
    }

    private fun createVpnInterface(): ParcelFileDescriptor? {
        val vpnInterface = Builder()
            .setSession("Singbox VPN")
            .addAddress(VPN_IP, VPN_PREFIX)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("8.8.8.8")
            .addDnsServer("1.1.1.1")
            .establish()

        if (vpnInterface == null) {
            Log.e("SingboxService", "VPN interface establish returned null")
        } else {
            Log.d("SingboxService", "VPN interface fd = ${vpnInterface.fd}")
        }
        return vpnInterface
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            "Singbox Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Used for Singbox foreground service"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val channelTest = manager.getNotificationChannel(NOTIFICATION_CHANNEL)
        if (channelTest == null) {
            Log.w("SingboxService", "Notification channel not found or deleted")
        } else if (channelTest.importance == NotificationManager.IMPORTANCE_NONE) {
            Log.w(
                "SingboxService",
                "Notification channel importance is NONE, notifications will not show"
            )
        }
    }

    private fun createNotification(text: String, intent: Intent?): Notification {
        val stopIntent = Intent(this, SingboxActionReceiver::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val restartIntent = Intent(this, SingboxActionReceiver::class.java).apply {
            action = ACTION_RESTART
            putExtra(EXTRA_CONFIG_PATH, intent?.getStringExtra(EXTRA_CONFIG_PATH))
        }
        val restartPendingIntent = PendingIntent.getBroadcast(
            this, 1, restartIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle("Singbox")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
            .addAction(android.R.drawable.ic_media_play, "Restart", restartPendingIntent)
            .build()
    }

    override fun onDestroy() {
        Log.d("SingboxService", "Service destroyed")
        try {
            SingboxNative.stop()
        } catch (e: Exception) {
            Log.e("SingboxService", "Error stopping SingboxNative", e)
        }

        try {
            tunInterface?.close()
            tunInterface = null
        } catch (e: Exception) {
            Log.e("SingboxService", "Error closing VPN interface", e)
        }

        _isRunning.value = false

        stopForeground(STOP_FOREGROUND_REMOVE)
        scope.cancel()
        mainScope.cancel()
        super.onDestroy()
    }

}
