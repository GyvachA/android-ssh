package com.gyvacha.androidssh.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.gyvacha.androidssh.service.SingboxService

class SingboxActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        val serviceIntent = Intent(context, SingboxService::class.java).apply {
            this.action = action
            putExtras(intent.extras ?: Bundle())
        }

        when (action) {
            SingboxService.ACTION_STOP -> {
                context.startService(serviceIntent)
            }
            SingboxService.ACTION_RESTART -> {
                context.startForegroundService(serviceIntent)
            }
        }
    }
}
