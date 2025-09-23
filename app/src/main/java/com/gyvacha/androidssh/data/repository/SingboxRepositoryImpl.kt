package com.gyvacha.androidssh.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import com.gyvacha.androidssh.domain.model.Status
import com.gyvacha.androidssh.domain.repository.SingboxRepository
import com.gyvacha.androidssh.receiver.SingboxActionReceiver
import com.gyvacha.androidssh.service.SingboxService
import com.gyvacha.androidssh.service.SingboxService.Companion.ACTION_STOP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class SingboxRepositoryImpl(
    private val context: Context
) : SingboxRepository {
    override val serviceStatus: StateFlow<Status> = SingboxService.serviceStatus
    private val _logs = MutableSharedFlow<String>(extraBufferCapacity = 100)
    override val logs: Flow<String> = _logs.asSharedFlow()
    private var logReceiver: BroadcastReceiver? = null
    private var startIntent: Intent? = null

    override suspend fun start(content: String?) {
        return withContext(Dispatchers.IO) {
            if (serviceStatus.value == Status.Started ||
                serviceStatus.value == Status.Starting ||
                serviceStatus.value == Status.Restarting
            ) {
                return@withContext
            }
            registerLogReceiver()
            startIntent = Intent(context, SingboxService::class.java).apply {
                putExtra(SingboxService.EXTRA_CONFIG_PATH, content)
            }
            startIntent?.let { ContextCompat.startForegroundService(context, it) }
        }
    }

    override suspend fun stop() {
        return withContext(Dispatchers.IO) {
            if (serviceStatus.value == Status.Stopped ||
                serviceStatus.value == Status.Stopping ||
                startIntent == null
            ) {
                return@withContext
            }
            val stopIntent = Intent(context, SingboxActionReceiver::class.java)
                .apply {
                    action = ACTION_STOP
                }
            context.sendBroadcast(stopIntent)
            unregisterLogReceiver()
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
        } else {
            0
        }
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
