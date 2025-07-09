package com.gyvacha.androidssh.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import com.gyvacha.androidssh.domain.repository.SingboxRepository
import com.gyvacha.androidssh.service.SingboxService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SingboxRepositoryImpl(
    private val context: Context
) : SingboxRepository {
    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    private val _logs = MutableSharedFlow<String>(extraBufferCapacity = 100)
    override val logs: Flow<String> = _logs.asSharedFlow()
    private var logReceiver: BroadcastReceiver? = null
    private var startIntent: Intent? = null

    override suspend fun start(configPath: String) {
        return withContext(Dispatchers.IO) {
            if (_isRunning.value) return@withContext
            registerLogReceiver()
            startIntent = Intent(context, SingboxService::class.java).apply {
                putExtra(SingboxService.EXTRA_CONFIG_PATH, configPath)
            }
            startIntent?.let { ContextCompat.startForegroundService(context, it) }
            _isRunning.value = true
        }

    }

    override suspend fun stop() {
        return withContext(Dispatchers.IO) {
            if (!_isRunning.value || startIntent == null) return@withContext
            context.stopService(startIntent)
            unregisterLogReceiver()
            _isRunning.value = false
        }
    }

    private fun registerLogReceiver() {
        if (logReceiver != null) return
        logReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val log = intent?.getStringExtra(SingboxService.EXTRA_LOG_LINE) ?: return
                _logs.tryEmit(log)
            }
        }
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Context.RECEIVER_NOT_EXPORTED
        } else 0
        context.registerReceiver(
            logReceiver,
            IntentFilter(SingboxService.ACTION_LOG),
            flag
        )
    }

    private fun unregisterLogReceiver() {
        logReceiver?.let {
            context.unregisterReceiver(it)
            logReceiver = null
        }
    }
}