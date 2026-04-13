package com.ratealert.currencytracker.data.repository

import com.ratealert.currencytracker.data.api.RetrofitClient
import com.ratealert.currencytracker.data.model.CurrencyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class CurrencyRepository {
    
    private val apiService = RetrofitClient.apiService
    
    suspend fun getRates(baseCurrency: String): Result<CurrencyResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRates(baseCurrency)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error("Failed to fetch rates: ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Network error occurred")
            }
        }
    }
    
    suspend fun getSpecificRate(
        baseCurrency: String,
        targetCurrency: String
    ): Result<Double> {
        return when (val result = getRates(baseCurrency)) {
            is Result.Success -> {
                val rate = result.data.rates[targetCurrency]
                if (rate != null) {
                    Result.Success(rate)
                } else {
                    Result.Error("Currency $targetCurrency not found")
                }
            }
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
