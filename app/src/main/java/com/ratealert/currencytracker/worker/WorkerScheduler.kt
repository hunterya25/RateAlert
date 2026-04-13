package com.ratealert.currencytracker.worker

import android.content.Context
import androidx.work.*
import com.ratealert.currencytracker.utils.Constants
import java.util.concurrent.TimeUnit

object WorkerScheduler {
    
    fun scheduleWork(context: Context, intervalMinutes: Long = 15) {
        cancelWork(context)
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<CurrencyWorker>(
            intervalMinutes.coerceAtLeast(15), TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(Constants.WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun scheduleRepeatingWork(context: Context, intervalMinutes: Long) {
        cancelWork(context)
        scheduleOneTimeWork(context, intervalMinutes)
    }
    
    fun scheduleOneTimeWork(context: Context, delayMinutes: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val oneTimeWork = OneTimeWorkRequestBuilder<CurrencyWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(Constants.WORK_TAG)
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            Constants.WORK_TAG,
            ExistingWorkPolicy.REPLACE,
            oneTimeWork
        )
    }
    
    fun runImmediately(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val immediateWork = OneTimeWorkRequestBuilder<CurrencyWorker>()
            .setConstraints(constraints)
            .addTag("${Constants.WORK_TAG}_immediate")
            .build()
        
        WorkManager.getInstance(context).enqueue(immediateWork)
    }
    
    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(Constants.WORK_TAG)
    }
}
