package com.ratealert.currencytracker.data.model

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("result") val result: String,
    @SerializedName("base_code") val baseCode: String,
    @SerializedName("rates") val rates: Map<String, Double>,
    @SerializedName("time_last_update_utc") val lastUpdate: String?
)

data class CurrencyPair(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val previousRate: Double = 0.0,
    val lastUpdated: String = ""
) {
    val changePercent: Double
        get() = if (previousRate > 0) ((rate - previousRate) / previousRate) * 100 else 0.0
    
    val isUp: Boolean get() = rate > previousRate
    val isDown: Boolean get() = rate < previousRate
}
