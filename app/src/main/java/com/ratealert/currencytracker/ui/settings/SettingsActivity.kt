package com.ratealert.currencytracker.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ratealert.currencytracker.R
import com.ratealert.currencytracker.databinding.ActivitySettingsBinding
import com.ratealert.currencytracker.utils.Constants
import com.ratealert.currencytracker.utils.PreferenceManager
import com.ratealert.currencytracker.worker.WorkerScheduler
import com.google.android.material.slider.Slider

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefManager = PreferenceManager(this)
        
        setupToolbar()
        setupCurrencySelectors()
        setupIntervalSlider()
        setupNotificationSettings()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
    
    private fun setupCurrencySelectors() {
        val currencies = Constants.POPULAR_CURRENCIES
        val displayList = currencies.map { 
            "${Constants.CURRENCY_FLAGS[it]} $it - ${Constants.CURRENCY_NAMES[it]}"
        }
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerBase.adapter = adapter
        binding.spinnerTarget.adapter = adapter
        
        // Set current selections
        val baseIndex = currencies.indexOf(prefManager.baseCurrency)
        val targetIndex = currencies.indexOf(prefManager.targetCurrency)
        
        if (baseIndex >= 0) binding.spinnerBase.setSelection(baseIndex)
        if (targetIndex >= 0) binding.spinnerTarget.setSelection(targetIndex)
        
        binding.spinnerBase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefManager.baseCurrency = currencies[position]
                updatePreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        binding.spinnerTarget.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefManager.targetCurrency = currencies[position]
                updatePreview()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupIntervalSlider() {
        val intervals = listOf(5, 10, 15, 30, 60)
        val currentInterval = prefManager.intervalMinutes
        val currentIndex = intervals.indexOf(currentInterval).coerceAtLeast(0)
        
        binding.sliderInterval.value = currentIndex.toFloat()
        binding.tvIntervalValue.text = "${intervals[currentIndex]} minutes"
        
        binding.sliderInterval.addOnChangeListener { _, value, _ ->
            val selectedInterval = intervals[value.toInt()]
            binding.tvIntervalValue.text = "$selectedInterval minutes"
            prefManager.intervalMinutes = selectedInterval
            
            // Reschedule worker with new interval
            if (selectedInterval < 15) {
                WorkerScheduler.scheduleRepeatingWork(this, selectedInterval.toLong())
            } else {
                WorkerScheduler.scheduleWork(this, selectedInterval.toLong())
            }
        }
    }
    
    private fun setupNotificationSettings() {
        binding.switchNotifications.isChecked = prefManager.notificationsEnabled
        binding.switchAlertOnChange.isChecked = prefManager.alertOnChange
        
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefManager.notificationsEnabled = isChecked
            binding.layoutAlertOptions.visibility = if (isChecked) View.VISIBLE else View.GONE
            
            if (isChecked) {
                WorkerScheduler.scheduleRepeatingWork(this, prefManager.intervalMinutes.toLong())
            } else {
                WorkerScheduler.cancelWork(this)
            }
        }
        
        binding.switchAlertOnChange.setOnCheckedChangeListener { _, isChecked ->
            prefManager.alertOnChange = isChecked
            binding.layoutThreshold.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        binding.sliderThreshold.value = prefManager.changeThreshold
        binding.tvThresholdValue.text = "${prefManager.changeThreshold}%"
        
        binding.sliderThreshold.addOnChangeListener { _, value, _ ->
            prefManager.changeThreshold = value
            binding.tvThresholdValue.text = String.format("%.1f%%", value)
        }
        
        // Initial visibility
        binding.layoutAlertOptions.visibility = 
            if (prefManager.notificationsEnabled) View.VISIBLE else View.GONE
        binding.layoutThreshold.visibility = 
            if (prefManager.alertOnChange) View.VISIBLE else View.GONE
    }
    
    private fun updatePreview() {
        val base = prefManager.baseCurrency
        val target = prefManager.targetCurrency
        val baseFlag = Constants.CURRENCY_FLAGS[base] ?: "💱"
        val targetFlag = Constants.CURRENCY_FLAGS[target] ?: "💱"
        binding.tvPairPreview.text = "$baseFlag $base → $targetFlag $target"
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
