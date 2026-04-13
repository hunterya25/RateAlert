package com.ratealert.currencytracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ratealert.currencytracker.data.model.CurrencyPair
import com.ratealert.currencytracker.data.model.CurrencyResponse
import com.ratealert.currencytracker.data.repository.CurrencyRepository
import com.ratealert.currencytracker.data.repository.Result
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    
    private val repository = CurrencyRepository()
    
    private val _currencyPair = MutableLiveData<CurrencyPair>()
    val currencyPair: LiveData<CurrencyPair> = _currencyPair
    
    private val _allRates = MutableLiveData<Map<String, Double>>()
    val allRates: LiveData<Map<String, Double>> = _allRates
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _calculatedAmount = MutableLiveData<Double>()
    val calculatedAmount: LiveData<Double> = _calculatedAmount
    
    private var previousRate = 0.0
    
    fun fetchRate(baseCurrency: String, targetCurrency: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = repository.getRates(baseCurrency)) {
                is Result.Success -> {
                    val rate = result.data.rates[targetCurrency] ?: 0.0
                    val currentTime = SimpleDateFormat(
                        "dd MMM yyyy, hh:mm:ss a", Locale.getDefault()
                    ).format(Date())
                    
                    _allRates.value = result.data.rates
                    
                    val pair = CurrencyPair(
                        baseCurrency = baseCurrency,
                        targetCurrency = targetCurrency,
                        rate = rate,
                        previousRate = previousRate,
                        lastUpdated = currentTime
                    )
                    _currencyPair.value = pair
                    previousRate = rate
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }
    
    fun calculateConversion(amount: Double, rate: Double): Double {
        val result = amount * rate
        _calculatedAmount.value = result
        return result
    }
    
    fun getPopularRates(baseCurrency: String, popularCurrencies: List<String>): List<Pair<String, Double>> {
        val rates = _allRates.value ?: return emptyList()
        return popularCurrencies
            .filter { it != baseCurrency }
            .mapNotNull { currency ->
                rates[currency]?.let { rate -> Pair(currency, rate) }
            }
    }
}
