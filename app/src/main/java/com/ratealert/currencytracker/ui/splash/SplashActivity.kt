package com.ratealert.currencytracker.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ratealert.currencytracker.databinding.ActivitySplashBinding
import com.ratealert.currencytracker.ui.home.HomeActivity
import com.ratealert.currencytracker.ui.onboarding.OnboardingActivity
import com.ratealert.currencytracker.utils.PreferenceManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var prefManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefManager = PreferenceManager(this)
        
        Handler(Looper.getMainLooper()).postDelayed({
            navigateNext()
        }, 2500)
    }
    
    private fun navigateNext() {
        val intent = if (prefManager.isOnboardingDone) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
