package com.gyvacha.androidssh.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.ServiceInfo
import android.net.IpPrefix
import android.net.ProxyInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.Status
import com.gyvacha.androidssh.receiver.SingboxActionReceiver
import com.gyvacha.androidssh.utils.SingboxConfigFileManager
import com.gyvacha.androidssh.utils.toList
import io.nekohasekai.libbox.BoxService
import io.nekohasekai.libbox.CommandServer
import io.nekohasekai.libbox.CommandServerHandler
import io.nekohasekai.libbox.ExchangeContext
import io.nekohasekai.libbox.InterfaceUpdateListener
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.LocalDNSTransport
import io.nekohasekai.libbox.PlatformInterface
import io.nekohasekai.libbox.StringIterator
import io.nekohasekai.libbox.SystemProxyStatus
import io.nekohasekai.libbox.TunOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.KeyStore
import kotlin.io.encoding.Base64

class SingboxService : VpnService() {

    private var boxService: BoxService? = null
    private var commandServer: CommandServer? = null
    private var tunInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val commandServerHandler = object : CommandServerHandler {
        override fun getSystemProxyStatus(): SystemProxyStatus {
            val status = SystemProxyStatus()
            status.available = false
            status.enabled = false
            return status
        }

        override fun postServiceClose() {
            scope.launch {
                stopService()
            }
        }

        override fun serviceReload() {
            scope.launch {
                restartService()
            }
        }

        override fun setSystemProxyEnabled(isEnabled: Boolean) {
            return
        }
    }

    private val platformInterface = object : PlatformInterface {
        override fun autoDetectInterfaceControl(fd: Int) {
            protect(fd)
        }

        override fun localDNSTransport(): LocalDNSTransport {
            return object : LocalDNSTransport {
                override fun exchange(
                    ctx: ExchangeContext?,
                    message: ByteArray?
                ) {
                    Log.d("LocalDNSTransport", "exchange method called")
                }

                override fun lookup(ctx: ExchangeContext?, network: String?, domain: String?) {
                    try {
                        if (domain != null && ctx != null) {
                            Log.d("LocalDNSTransport", "Looking up domain: $domain")

                            val addresses = InetAddress.getAllByName(domain)

                            if (addresses.isNotEmpty()) {
                                val result =
                                    addresses.mapNotNull { it.hostAddress }.joinToString("\n")

                                Log.d("LocalDNSTransport", "Resolved $domain to: $result")

                                ctx.success(result)
                            } else {
                                Log.w("LocalDNSTransport", "No addresses found for $domain")
                                ctx.errorCode(3)
                            }
                        }
                    } catch (e: UnknownHostException) {
                        Log.e("LocalDNSTransport", "DNS lookup failed for $domain: ${e.message}")
                        ctx?.errorCode(3)
                    } catch (e: Exception) {
                        Log.e(
                            "LocalDNSTransport",
                            "Unexpected error during DNS lookup for $domain",
                            e
                        )
                        ctx?.errorCode(2)
                    }
                }

                override fun raw(): Boolean {
                    return false
                }
            }
        }

        override fun systemCertificates(): StringIterator {
            val certificates = mutableListOf<String>()
            val keyStore = KeyStore.getInstance("AndroidCAStore")
            if (keyStore != null) {
                keyStore.load(null, null)
                val aliases = keyStore.aliases()
                while (aliases.hasMoreElements()) {
                    val cert = keyStore.getCertificate(aliases.nextElement())
                    certificates.add(
                        "-----BEGIN CERTIFICATE-----\n" +
                            Base64.encode(cert.encoded) +
                            "\n-----END CERTIFICATE-----"
                    )
                }
            }
            return object : StringIterator {
                val certificatesIterator = certificates.iterator()

                override fun hasNext(): Boolean {
                    return certificatesIterator.hasNext()
                }

                override fun len(): Int {
                    return 0
                }

                override fun next(): String? {
                    return certificatesIterator.next()
                }
            }
        }

        override fun clearDNSCache() {
            return
        }

        override fun closeDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
            Log.d(SINGBOX_SERVICE_TAG, "Closing default interface monitor")
        }

        override fun findConnectionOwner(
            ipProtocol: Int,
            sourceAddress: String?,
            sourcePort: Int,
            destinationAddress: String?,
            destinationPort: Int
        ): Int {
            return -1
        }

        override fun getInterfaces(): io.nekohasekai.libbox.NetworkInterfaceIterator? {
            return null
        }

        override fun includeAllNetworks(): Boolean {
            return false
        }

        override fun openTun(options: TunOptions): Int {
            val builder = Builder()
                .setSession("sing-box")
                .setMtu(options.mtu)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setMetered(false)
            }

            val inet4Address = options.inet4Address
            while (inet4Address.hasNext()) {
                val address = inet4Address.next()
                builder.addAddress(address.address(), address.prefix())
            }

            val inet6Address = options.inet6Address
            while (inet6Address.hasNext()) {
                val address = inet6Address.next()
                builder.addAddress(address.address(), address.prefix())
            }

            if (options.autoRoute) {
                builder.addDnsServer(options.dnsServerAddress.value)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val inet4RouteAddress = options.inet4RouteAddress
                    if (inet4RouteAddress.hasNext()) {
                        while (inet4RouteAddress.hasNext()) {
                            val ipPrefix = inet4RouteAddress.next()
                            builder.addRoute(ipPrefix.address(), ipPrefix.prefix())
                        }
                    } else if (options.inet4Address.hasNext()) {
                        builder.addRoute("0.0.0.0", 0)
                    }

                    val inet6RouteAddress = options.inet6RouteAddress
                    if (inet6RouteAddress.hasNext()) {
                        while (inet6RouteAddress.hasNext()) {
                            val ipPrefix = inet6RouteAddress.next()
                            builder.addRoute(ipPrefix.address(), ipPrefix.prefix())
                        }
                    } else if (options.inet6Address.hasNext()) {
                        builder.addRoute("::", 0)
                    }

                    val inet4RouteExcludeAddress = options.inet4RouteExcludeAddress
                    while (inet4RouteExcludeAddress.hasNext()) {
                        val ipPrefix = inet4RouteExcludeAddress.next()
                        try {
                            val inetAddress = InetAddress.getByName(ipPrefix.address())
                            val excludePrefix = IpPrefix(inetAddress, ipPrefix.prefix())
                            builder.excludeRoute(excludePrefix)
                        } catch (e: Exception) {
                            Log.e(
                                SINGBOX_SERVICE_TAG,
                                "Error excluding IPv4 route: ${ipPrefix.address()}/${ipPrefix.prefix()}",
                                e
                            )
                        }
                    }

                    val inet6RouteExcludeAddress = options.inet6RouteExcludeAddress
                    while (inet6RouteExcludeAddress.hasNext()) {
                        val ipPrefix = inet6RouteExcludeAddress.next()
                        try {
                            val inetAddress = InetAddress.getByName(ipPrefix.address())
                            val excludePrefix = IpPrefix(inetAddress, ipPrefix.prefix())
                            builder.excludeRoute(excludePrefix)
                        } catch (e: Exception) {
                            Log.e(
                                SINGBOX_SERVICE_TAG,
                                "Error excluding IPv6 route: ${ipPrefix.address()}/${ipPrefix.prefix()}",
                                e
                            )
                        }
                    }
                } else {
                    val inet4RouteAddress = options.inet4RouteRange
                    if (inet4RouteAddress.hasNext()) {
                        while (inet4RouteAddress.hasNext()) {
                            val address = inet4RouteAddress.next()
                            builder.addRoute(address.address(), address.prefix())
                        }
                    } else {
                        builder.addRoute("0.0.0.0", 0)
                    }

                    val inet6RouteAddress = options.inet6RouteRange
                    if (inet6RouteAddress.hasNext()) {
                        while (inet6RouteAddress.hasNext()) {
                            val address = inet6RouteAddress.next()
                            builder.addRoute(address.address(), address.prefix())
                        }
                    } else {
                        builder.addRoute("::", 0)
                    }
                }

                val includePackage = options.includePackage
                if (includePackage.hasNext()) {
                    while (includePackage.hasNext()) {
                        try {
                            builder.addAllowedApplication(includePackage.next())
                        } catch (_: NameNotFoundException) {
                        }
                    }
                }

                val excludePackage = options.excludePackage
                if (excludePackage.hasNext()) {
                    while (excludePackage.hasNext()) {
                        try {
                            builder.addDisallowedApplication(excludePackage.next())
                        } catch (_: NameNotFoundException) {
                        }
                    }
                }

                try {
                    builder.addDisallowedApplication(packageName)
                } catch (_: NameNotFoundException) {
                }
            }

            if (options.isHTTPProxyEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setHttpProxy(
                    ProxyInfo.buildDirectProxy(
                        options.httpProxyServer,
                        options.httpProxyServerPort,
                        options.httpProxyBypassDomain.toList()
                    )
                )
            }

            val pfd = builder.establish()
                ?: error("Android: the application is not prepared or is revoked")
            tunInterface = pfd
            return pfd.fd
        }

        override fun packageNameByUid(uid: Int): String? {
            return try {
                val pm = packageManager
                val packages = pm.getPackagesForUid(uid)
                packages?.firstOrNull()
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error getting package name for uid $uid", e)
                null
            }
        }

        override fun readWIFIState(): io.nekohasekai.libbox.WIFIState? {
            return null
        }

        override fun sendNotification(notification: io.nekohasekai.libbox.Notification?) {
            notification?.let { notif ->
                scope.launch {
                    Log.d(SINGBOX_SERVICE_TAG, "Notification: ${notif.title} - ${notif.body}")
//                    sendLog("Notification: ${notif.title} - ${notif.body}")
                }
            }
        }

        override fun startDefaultInterfaceMonitor(listener: InterfaceUpdateListener?) {
            Log.d(SINGBOX_SERVICE_TAG, "Starting default interface monitor")
        }

        override fun uidByPackageName(packageName: String?): Int {
            return try {
                if (packageName == null) return -1
                val pm = packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)
                appInfo.uid
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error getting uid for package $packageName", e)
                -1
            }
        }

        override fun underNetworkExtension(): Boolean {
            return false
        }

        override fun usePlatformAutoDetectInterfaceControl(): Boolean {
            return true
        }

        override fun useProcFS(): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        }

        override fun writeLog(message: String?) {
            scope.launch {
                message?.let { sendLog(it) }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(SINGBOX_SERVICE_TAG, "onStartCommand: starting foreground service")

        when (intent?.action) {
            ACTION_STOP -> {
                scope.launch {
                    stopService()
                }
                return START_NOT_STICKY
            }

            ACTION_RESTART -> {
                scope.launch {
                    restartService()
                }
                return START_STICKY
            }
        }

        createNotificationChannel()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(getString(R.string.singbox_service_starting), intent),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(getString(R.string.singbox_service_starting), intent)
                )
            }
        } catch (e: Exception) {
            Log.e(SINGBOX_SERVICE_TAG, "Failed to start foreground", e)
            stopSelf()
            return START_NOT_STICKY
        }

        scope.launch {
            startServiceSingbox()
        }

        return START_STICKY
    }

    private suspend fun startServiceSingbox() = withContext(Dispatchers.IO) {
        try {
            _serviceStatus.update { Status.Starting }

            withContext(Dispatchers.Main) {
                updateNotification(getString(R.string.singbox_service_starting))
            }

            if (prepare(this@SingboxService) != null) {
                Log.e(SINGBOX_SERVICE_TAG, "VPN permission not granted")
                stopService()
                return@withContext
            }

            val workDir = File(filesDir, "singbox")
            if (!workDir.exists()) {
                workDir.mkdirs()
            }

            val configFileManager = SingboxConfigFileManager(this@SingboxService)
            val configFile = configFileManager.getFile()

            if (!configFile.exists() || !configFile.canRead()) {
                Log.e(SINGBOX_SERVICE_TAG, "Config file not accessible")
                stopService()
                return@withContext
            }

            val configContent = configFile.readText()
            Log.d(SINGBOX_SERVICE_TAG, "Config content length: ${configContent.length}")

            if (configContent.isBlank()) {
                Log.e(SINGBOX_SERVICE_TAG, "Config file is empty")
                stopService()
                return@withContext
            }

            Libbox.setMemoryLimit(false)

            val newService = try {
                Libbox.newService(configContent, platformInterface)
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error creating BoxService: ${e.localizedMessage}", e)
                stopService()
                return@withContext
            }

            startCommandServer()

            commandServer?.setService(newService)

            try {
                newService.start()
                boxService = newService
                _serviceStatus.update { Status.Started }

                withContext(Dispatchers.Main) {
                    updateNotification(getString(R.string.singbox_service_started))
                }

                sendLog("Singbox started successfully")
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error starting BoxService: ${e.localizedMessage}", e)
                try {
                    newService.close()
                } catch (closeException: Exception) {
                    Log.e(SINGBOX_SERVICE_TAG, "Error closing failed service", closeException)
                }
                stopService()
                return@withContext
            }
        } catch (e: Exception) {
            Log.e(SINGBOX_SERVICE_TAG, "Error in startService: ${e.localizedMessage}", e)
            stopService()
        }
    }

    private fun startCommandServer() {
        try {
            val workDir = File(cacheDir, "singbox")
            if (!workDir.exists()) {
                workDir.mkdirs()
            }

            val commandServer = CommandServer(commandServerHandler, COMMAND_SERVER_MAX_LINES)
            commandServer.start()
            this.commandServer = commandServer
            Log.d(SINGBOX_SERVICE_TAG, "Command server started in ${workDir.absolutePath}")
        } catch (e: Exception) {
            Log.e(SINGBOX_SERVICE_TAG, "Error starting command server", e)
        }
    }

    private suspend fun restartService() {
        Log.d(SINGBOX_SERVICE_TAG, "Restarting service")
        _serviceStatus.update { Status.Restarting }

        boxService?.let { service ->
            try {
                service.close()
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error closing BoxService during restart", e)
            }
        }
        boxService = null

        tunInterface?.close()
        tunInterface = null

        startServiceSingbox()
    }

    private suspend fun stopService() {
        Log.d(SINGBOX_SERVICE_TAG, "Stopping service")
        _serviceStatus.update { Status.Stopping }

        boxService?.let { service ->
            try {
                service.close()
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error closing BoxService", e)
            }
        }
        boxService = null

        commandServer?.let { server ->
            try {
                server.close()
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Error closing command server", e)
            }
        }
        commandServer = null

        try {
            tunInterface?.close()
            tunInterface = null
        } catch (e: Exception) {
            Log.e(SINGBOX_SERVICE_TAG, "Error closing VPN interface", e)
        }

        _serviceStatus.update { Status.Stopped }

        withContext(Dispatchers.Main) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private suspend fun sendLog(log: String) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(ACTION_LOG).apply {
                    putExtra(EXTRA_LOG_LINE, log)
                }
                sendBroadcast(intent)
                Log.d(SINGBOX_SERVICE_TAG, log)
            } catch (e: Exception) {
                Log.e(SINGBOX_SERVICE_TAG, "Failed to send broadcast", e)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            "Singbox Service",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Used for Singbox foreground service"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(text: String, intent: Intent?): Notification {
        val stopIntent = Intent(this, SingboxActionReceiver::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val restartIntent = Intent(this, SingboxActionReceiver::class.java).apply {
            action = ACTION_RESTART
            putExtra(EXTRA_CONFIG_PATH, intent?.getStringExtra(EXTRA_CONFIG_PATH))
        }
        val restartPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            restartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle("Singbox VPN")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setShowWhen(false)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopPendingIntent)
            .addAction(android.R.drawable.ic_media_play, "Restart", restartPendingIntent)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = createNotification(text, null)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        Log.d(SINGBOX_SERVICE_TAG, "Service destroying")
        scope.launch {
            stopService()
        }
        scope.cancel()
        super.onDestroy()
    }

    override fun onRevoke() {
        Log.d(SINGBOX_SERVICE_TAG, "VPN permission revoked")
        scope.launch {
            stopService()
        }
        super.onRevoke()
    }

    companion object {
        const val EXTRA_CONFIG_PATH = "config_path"
        const val EXTRA_LOG_LINE = "log_line"
        const val ACTION_LOG = "singbox_log_broadcast"
        const val NOTIFICATION_CHANNEL = "singbox_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "singbox_stop"
        const val ACTION_RESTART = "singbox_restart"

        private val _serviceStatus = MutableStateFlow(Status.Stopped)
        val serviceStatus: StateFlow<Status> = _serviceStatus.asStateFlow()

        private const val SINGBOX_SERVICE_TAG = "SingboxService"
        private const val COMMAND_SERVER_MAX_LINES = 300
    }
}
