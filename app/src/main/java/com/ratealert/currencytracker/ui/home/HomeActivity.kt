package com.ratealert.currencytracker.ui.home

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.ratealert.currencytracker.R
import com.ratealert.currencytracker.databinding.ActivityHomeBinding
import com.ratealert.currencytracker.notification.NotificationHelper
import com.ratealert.currencytracker.ui.settings.SettingsActivity
import com.ratealert.currencytracker.utils.Constants
import com.ratealert.currencytracker.utils.PreferenceManager
import com.ratealert.currencytracker.worker.WorkerScheduler

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefManager: PreferenceManager
    private val viewModel: HomeViewModel by viewModels()
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        startWorker()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefManager = PreferenceManager(this)
        
        NotificationHelper.createNotificationChannel(this)
        checkPermissions()
        setupUI()
        observeViewModel()
        fetchCurrentRate()
    }
    
    override fun onResume() {
        super.onResume()
        fetchCurrentRate()
    }
    
    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startWorker()
        }
    }
    
    private fun startWorker() {
        val interval = prefManager.intervalMinutes.toLong()
        if (interval < 15) {
            WorkerScheduler.scheduleRepeatingWork(this, interval)
        } else {
            WorkerScheduler.scheduleWork(this, interval)
        }
        WorkerScheduler.runImmediately(this)
    }
    
    private fun setupUI() {
        // Base Currency
        val baseCurrency = prefManager.baseCurrency
        val targetCurrency = prefManager.targetCurrency
        
        binding.tvBaseCurrency.text = baseCurrency
        binding.tvTargetCurrency.text = targetCurrency
        binding.tvBaseFlag.text = Constants.CURRENCY_FLAGS[baseCurrency] ?: "💱"
        binding.tvTargetFlag.text = Constants.CURRENCY_FLAGS[targetCurrency] ?: "💱"
        binding.tvBaseName.text = Constants.CURRENCY_NAMES[baseCurrency] ?: baseCurrency
        binding.tvTargetName.text = Constants.CURRENCY_NAMES[targetCurrency] ?: targetCurrency
        
        // Settings
        binding.fabSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        
        // Refresh
        binding.btnRefresh.setOnClickListener {
            val refreshAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_360)
            binding.btnRefresh.startAnimation(refreshAnim)
            fetchCurrentRate()
        }
        
        // Swap currencies
        binding.btnSwap.setOnClickListener {
            val currentBase = prefManager.baseCurrency
            val currentTarget = prefManager.targetCurrency
            prefManager.baseCurrency = currentTarget
            prefManager.targetCurrency = currentBase
            
            val anim = AnimationUtils.loadAnimation(this, R.anim.rotate_360)
            binding.btnSwap.startAnimation(anim)
            
            setupUI()
            fetchCurrentRate()
        }
        
        // Calculator
        setupCalculator()
        
        // Interval chip
        binding.tvInterval.text = "${prefManager.intervalMinutes} min"
    }
    
    private fun setupCalculator() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amount = s.toString().toDoubleOrNull() ?: 0.0
                prefManager.calcAmount = amount.toFloat()
                val rate = prefManager.lastRate.toDouble()
                if (rate > 0) {
                    val result = viewModel.calculateConversion(amount, rate)
                    binding.tvResult.text = String.format("%.4f", result)
                    binding.tvCalcTargetCurrency.text = prefManager.targetCurrency
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        
        // Restore saved amount
        val savedAmount = prefManager.calcAmount
        if (savedAmount > 0) {
            binding.etAmount.setText(savedAmount.toString())
        }
        
        binding.tvCalcBaseCurrency.text = prefManager.baseCurrency
        binding.tvCalcTargetCurrency.text = prefManager.targetCurrency
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.shimmerLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.cardRateMain.visibility = if (isLoading) View.GONE else View.VISIBLE
            if (isLoading) binding.shimmerLayout.startShimmer()
            else binding.shimmerLayout.stopShimmer()
        }
        
        viewModel.currencyPair.observe(this) { pair ->
            // Save to prefs
            prefManager.lastRate = pair.rate.toFloat()
            prefManager.lastUpdate = pair.lastUpdated
            
            // Animate rate change
            animateRateChange(pair.rate)
            
            binding.tvRate.text = String.format("%.4f", pair.rate)
            binding.tvLastUpdate.text = "Updated: ${pair.lastUpdated}"
            
            // Show change
            if (pair.previousRate > 0) {
                val changeText = String.format("%.4f", pair.rate - pair.previousRate)
                val changePercent = String.format("%.2f%%", pair.changePercent)
                
                if (pair.isUp) {
                    binding.tvChange.text = "+$changeText ($changePercent)"
                    binding.tvChange.setTextColor(getColor(R.color.green))
                    binding.ivTrend.setImageResource(R.drawable.ic_trend_up)
                    binding.ivTrend.setColorFilter(getColor(R.color.green))
                } else if (pair.isDown) {
                    binding.tvChange.text = "$changeText ($changePercent)"
                    binding.tvChange.setTextColor(getColor(R.color.red))
                    binding.ivTrend.setImageResource(R.drawable.ic_trend_down)
                    binding.ivTrend.setColorFilter(getColor(R.color.red))
                } else {
                    binding.tvChange.text = "No change"
                    binding.tvChange.setTextColor(getColor(R.color.text_secondary))
                }
                binding.ivTrend.visibility = View.VISIBLE
                binding.tvChange.visibility = View.VISIBLE
            }
            
            // Update calculator
            val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 1.0
            val result = viewModel.calculateConversion(amount, pair.rate)
            binding.tvResult.text = String.format("%.4f", result)
            binding.tvCalcTargetCurrency.text = pair.targetCurrency
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                binding.tvError.text = it
                binding.tvError.visibility = View.VISIBLE
                binding.cardRateMain.visibility = View.GONE
            } ?: run {
                binding.tvError.visibility = View.GONE
            }
        }
    }
    
    private fun animateRateChange(newRate: Double) {
        ObjectAnimator.ofFloat(binding.tvRate, "alpha", 0f, 1f).apply {
            duration = 500
            start()
        }
        ObjectAnimator.ofFloat(binding.tvRate, "translationY", -20f, 0f).apply {
            duration = 500
            start()
        }
    }
    
    private fun fetchCurrentRate() {
        viewModel.fetchRate(prefManager.baseCurrency, prefManager.targetCurrency)
    }
}
