package com.ratealert.currencytracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ratealert.currencytracker.notification.NotificationHelper
import com.ratealert.currencytracker.utils.PreferenceManager
import com.ratealert.currencytracker.worker.WorkerScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefManager = PreferenceManager(context)
            NotificationHelper.createNotificationChannel(context)
            
            if (prefManager.notificationsEnabled) {
                val interval = prefManager.intervalMinutes.toLong()
                if (interval < 15) {
                    WorkerScheduler.scheduleRepeatingWork(context, interval)
                } else {
                    WorkerScheduler.scheduleWork(context, interval)
                }
            }
        }
    }
}
