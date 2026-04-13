package com.ratealert.currencytracker.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.ratealert.currencytracker.databinding.ActivityOnboardingBinding
import com.ratealert.currencytracker.ui.home.HomeActivity
import com.ratealert.currencytracker.utils.PreferenceManager

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var prefManager: PreferenceManager
    
    private val pages = listOf(
        OnboardingPage(
            "Live Exchange Rates",
            "Get real-time currency rates from around the world. Always stay updated with the latest prices.",
            "lottie_world"
        ),
        OnboardingPage(
            "Smart Notifications",
            "Set your preferred currency pair and receive automatic alerts every 5 minutes. Never miss a rate change!",
            "lottie_notification"
        ),
        OnboardingPage(
            "Currency Calculator",
            "Instantly convert any amount between currencies using live rates. Plan your finances smarter.",
            "lottie_calculator"
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefManager = PreferenceManager(this)
        
        setupViewPager()
        setupButtons()
    }
    
    private fun setupViewPager() {
        val adapter = OnboardingAdapter(pages)
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)
        
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == pages.size - 1) {
                    binding.btnNext.text = "Get Started"
                    binding.tvSkip.text = ""
                } else {
                    binding.btnNext.text = "Next"
                    binding.tvSkip.text = "Skip"
                }
            }
        })
    }
    
    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.size - 1) {
                binding.viewPager.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }
        
        binding.tvSkip.setOnClickListener {
            finishOnboarding()
        }
    }
    
    private fun finishOnboarding() {
        prefManager.isOnboardingDone = true
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val animation: String
)
