package com.ratealert.currencytracker.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ratealert.currencytracker.R
import com.ratealert.currencytracker.databinding.ItemOnboardingBinding

class OnboardingAdapter(private val pages: List<OnboardingPage>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
    
    inner class OnboardingViewHolder(val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OnboardingViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val page = pages[position]
        with(holder.binding) {
            tvTitle.text = page.title
            tvDescription.text = page.description
            
            val iconRes = when (position) {
                0 -> R.mipmap.ic_launcher
                1 -> R.drawable.ic_notification
                else -> R.mipmap.ic_launcher // Fallback for calculator page
            }
            ivIcon.setImageResource(iconRes)
        }
    }
    
    override fun getItemCount() = pages.size
}
