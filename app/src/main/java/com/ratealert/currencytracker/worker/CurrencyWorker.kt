package com.ratealert.currencytracker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ratealert.currencytracker.data.repository.CurrencyRepository
import com.ratealert.currencytracker.data.repository.Result
import com.ratealert.currencytracker.notification.NotificationHelper
import com.ratealert.currencytracker.utils.PreferenceManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrencyWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val repository = CurrencyRepository()
    private val prefManager = PreferenceManager(context)
    
    override suspend fun doWork(): Result {
        return try {
            if (!prefManager.notificationsEnabled) {
                return Result.success()
            }
            
            val baseCurrency = prefManager.baseCurrency
            val targetCurrency = prefManager.targetCurrency
            val previousRate = prefManager.lastRate.toDouble()
            
            when (val result = repository.getSpecificRate(baseCurrency, targetCurrency)) {
                is com.ratealert.currencytracker.data.repository.Result.Success -> {
                    val currentRate = result.data
                    val currentTime = SimpleDateFormat(
                        "dd MMM, hh:mm a", Locale.getDefault()
                    ).format(Date())
                    
                    val shouldNotify = if (prefManager.alertOnChange) {
                        val changePercent = if (previousRate > 0) {
                            Math.abs((currentRate - previousRate) / previousRate) * 100
                        } else 100.0
                        changePercent >= prefManager.changeThreshold
                    } else {
                        true
                    }
                    
                    if (shouldNotify) {
                        NotificationHelper.sendRateNotification(
                            applicationContext,
                            baseCurrency,
                            targetCurrency,
                            currentRate,
                            previousRate,
                            currentTime
                        )
                    }
                    
                    prefManager.previousRate = prefManager.lastRate
                    prefManager.lastRate = currentRate.toFloat()
                    prefManager.lastUpdate = currentTime
                    
                    val intervalMinutes = prefManager.intervalMinutes.toLong()
                    if (intervalMinutes < 15) {
                        WorkerScheduler.scheduleOneTimeWork(applicationContext, intervalMinutes)
                    }
                    
                    Result.success()
                }
                is com.ratealert.currencytracker.data.repository.Result.Error -> {
                    Result.retry()
                }
                else -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
