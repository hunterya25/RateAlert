package com.ratealert.currencytracker.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME, Context.MODE_PRIVATE
    )
    
    var isOnboardingDone: Boolean
        get() = prefs.getBoolean(Constants.KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_ONBOARDING_DONE, value).apply()
    
    var baseCurrency: String
        get() = prefs.getString(Constants.KEY_BASE_CURRENCY, Constants.DEFAULT_BASE) ?: Constants.DEFAULT_BASE
        set(value) = prefs.edit().putString(Constants.KEY_BASE_CURRENCY, value).apply()
    
    var targetCurrency: String
        get() = prefs.getString(Constants.KEY_TARGET_CURRENCY, Constants.DEFAULT_TARGET) ?: Constants.DEFAULT_TARGET
        set(value) = prefs.edit().putString(Constants.KEY_TARGET_CURRENCY, value).apply()
    
    var intervalMinutes: Int
        get() = prefs.getInt(Constants.KEY_INTERVAL_MINUTES, Constants.DEFAULT_INTERVAL)
        set(value) = prefs.edit().putInt(Constants.KEY_INTERVAL_MINUTES, value).apply()
    
    var lastRate: Float
        get() = prefs.getFloat(Constants.KEY_LAST_RATE, 0f)
        set(value) = prefs.edit().putFloat(Constants.KEY_LAST_RATE, value).apply()
    
    var lastUpdate: String
        get() = prefs.getString(Constants.KEY_LAST_UPDATE, "") ?: ""
        set(value) = prefs.edit().putString(Constants.KEY_LAST_UPDATE, value).apply()
    
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, value).apply()
    
    var alertOnChange: Boolean
        get() = prefs.getBoolean(Constants.KEY_ALERT_ON_CHANGE, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_ALERT_ON_CHANGE, value).apply()
    
    var changeThreshold: Float
        get() = prefs.getFloat(Constants.KEY_CHANGE_THRESHOLD, 0.5f)
        set(value) = prefs.edit().putFloat(Constants.KEY_CHANGE_THRESHOLD, value).apply()
    
    // Save multiple currency pairs as JSON string
    var watchedPairs: String
        get() = prefs.getString("watched_pairs", "") ?: ""
        set(value) = prefs.edit().putString("watched_pairs", value).apply()
    
    var previousRate: Float
        get() = prefs.getFloat("previous_rate", 0f)
        set(value) = prefs.edit().putFloat("previous_rate", value).apply()
    
    // Calculator amount
    var calcAmount: Float
        get() = prefs.getFloat("calc_amount", 1f)
        set(value) = prefs.edit().putFloat("calc_amount", value).apply()
}
