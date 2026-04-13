package com.ratealert.currencytracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ratealert.currencytracker.R
import com.ratealert.currencytracker.ui.home.HomeActivity
import com.ratealert.currencytracker.utils.Constants

object NotificationHelper {
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live currency rate alerts"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun sendRateNotification(
        context: Context,
        baseCurrency: String,
        targetCurrency: String,
        rate: Double,
        previousRate: Double,
        lastUpdate: String
    ) {
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val changePercent = if (previousRate > 0) {
            ((rate - previousRate) / previousRate) * 100
        } else 0.0
        
        val isUp = rate > previousRate && previousRate > 0
        val isDown = rate < previousRate && previousRate > 0
        
        val baseFlag = Constants.CURRENCY_FLAGS[baseCurrency] ?: ""
        val targetFlag = Constants.CURRENCY_FLAGS[targetCurrency] ?: ""
        
        val trendIcon = when {
            isUp -> "📈"
            isDown -> "📉"
            else -> "💱"
        }
        
        val changeText = when {
            isUp -> String.format("+%.2f%%", changePercent)
            isDown -> String.format("%.2f%%", changePercent)
            else -> "No change"
        }
        
        val formattedRate = String.format("%.4f", rate)
        val title = "$trendIcon $baseFlag $baseCurrency → $targetFlag $targetCurrency"
        val message = "Rate: $formattedRate   $changeText"
        val bigText = """
            Current Rate: 1 $baseCurrency = $formattedRate $targetCurrency
            Change: $changeText
            Updated: $lastUpdate
        """.trimIndent()
        
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            //.setColor(context.getColor(R.color.primary))
            .setColorized(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
        
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }
}
