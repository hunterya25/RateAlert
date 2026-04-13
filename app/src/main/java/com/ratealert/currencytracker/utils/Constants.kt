package com.ratealert.currencytracker.utils

object Constants {
    const val BASE_URL = "https://open.er-api.com/v6/"
    const val WORK_TAG = "currency_rate_work"
    const val NOTIFICATION_CHANNEL_ID = "rate_alert_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Rate Alerts"
    const val NOTIFICATION_ID = 1001
    
    const val PREF_NAME = "rate_alert_prefs"
    const val KEY_ONBOARDING_DONE = "onboarding_done"
    const val KEY_BASE_CURRENCY = "base_currency"
    const val KEY_TARGET_CURRENCY = "target_currency"
    const val KEY_INTERVAL_MINUTES = "interval_minutes"
    const val KEY_LAST_RATE = "last_rate"
    const val KEY_LAST_UPDATE = "last_update"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_ALERT_ON_CHANGE = "alert_on_change"
    const val KEY_CHANGE_THRESHOLD = "change_threshold"
    
    const val DEFAULT_BASE = "USD"
    const val DEFAULT_TARGET = "INR"
    const val DEFAULT_INTERVAL = 5
    
    val POPULAR_CURRENCIES = listOf(
        "USD", "INR", "EUR", "GBP", "JPY", "CAD", 
        "AUD", "CHF", "CNY", "SGD", "AED", "SAR",
        "HKD", "NZD", "SEK", "NOK", "MXN", "BRL",
        "ZAR", "KRW", "THB", "MYR", "IDR", "PHP"
    )
    
    val CURRENCY_NAMES = mapOf(
        "USD" to "US Dollar",
        "INR" to "Indian Rupee",
        "EUR" to "Euro",
        "GBP" to "British Pound",
        "JPY" to "Japanese Yen",
        "CAD" to "Canadian Dollar",
        "AUD" to "Australian Dollar",
        "CHF" to "Swiss Franc",
        "CNY" to "Chinese Yuan",
        "SGD" to "Singapore Dollar",
        "AED" to "UAE Dirham",
        "SAR" to "Saudi Riyal",
        "HKD" to "Hong Kong Dollar",
        "NZD" to "New Zealand Dollar",
        "SEK" to "Swedish Krona",
        "NOK" to "Norwegian Krone",
        "MXN" to "Mexican Peso",
        "BRL" to "Brazilian Real",
        "ZAR" to "South African Rand",
        "KRW" to "South Korean Won",
        "THB" to "Thai Baht",
        "MYR" to "Malaysian Ringgit",
        "IDR" to "Indonesian Rupiah",
        "PHP" to "Philippine Peso"
    )
    
    val CURRENCY_FLAGS = mapOf(
        "USD" to "🇺🇸",
        "INR" to "🇮🇳",
        "EUR" to "🇪🇺",
        "GBP" to "🇬🇧",
        "JPY" to "🇯🇵",
        "CAD" to "🇨🇦",
        "AUD" to "🇦🇺",
        "CHF" to "🇨🇭",
        "CNY" to "🇨🇳",
        "SGD" to "🇸🇬",
        "AED" to "🇦🇪",
        "SAR" to "🇸🇦",
        "HKD" to "🇭🇰",
        "NZD" to "🇳🇿",
        "SEK" to "🇸🇪",
        "NOK" to "🇳🇴",
        "MXN" to "🇲🇽",
        "BRL" to "🇧🇷",
        "ZAR" to "🇿🇦",
        "KRW" to "🇰🇷",
        "THB" to "🇹🇭",
        "MYR" to "🇲🇾",
        "IDR" to "🇮🇩",
        "PHP" to "🇵🇭"
    )
}
