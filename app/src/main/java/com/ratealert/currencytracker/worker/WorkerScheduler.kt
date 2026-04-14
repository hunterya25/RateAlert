package com.ratealert.currencytracker.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.*
import com.ratealert.currencytracker.receiver.AlarmReceiver
import com.ratealert.currencytracker.utils.Constants
import java.util.concurrent.TimeUnit

object WorkerScheduler {
    
    private const val ALARM_REQUEST_CODE = 1001
    
    fun scheduleExactAlarm(context: Context, delayMinutes: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerAtMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(delayMinutes)
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            val fallbackTrigger = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(delayMinutes)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                fallbackTrigger,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleWork(context: Context, intervalMinutes: Long = 15) {
        cancelWork(context)
        scheduleExactAlarm(context, intervalMinutes)
    }
    
    fun scheduleRepeatingWork(context: Context, intervalMinutes: Long) {
        cancelWork(context)
        scheduleExactAlarm(context, intervalMinutes)
    }
    
    fun scheduleOneTimeWork(context: Context, delayMinutes: Long) {
        scheduleExactAlarm(context, delayMinutes)
    }
    
    fun runImmediately(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        context.sendBroadcast(intent)
    }
    
    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(Constants.WORK_TAG)
        cancelAlarm(context)
    }
}
