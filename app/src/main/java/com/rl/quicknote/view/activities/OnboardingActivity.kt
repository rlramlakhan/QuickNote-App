package com.rl.quicknote.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.rl.quicknote.R
import com.rl.quicknote.databinding.ActivityOnboardingBinding
import com.rl.quicknote.view.adapters.OnboardingAdapter
import com.rl.quicknote.viewmodel.OnboardingViewModel
import androidx.core.content.edit

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingViewModel: OnboardingViewModel
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (isOnboardingCompleted()) {
            navigateToAuth()
            return
        }

        onboardingViewModel = ViewModelProvider(this)[OnboardingViewModel::class.java]
        onboardingAdapter = OnboardingAdapter(onboardingViewModel.onboardingItems)

        binding.viewPager.adapter = onboardingAdapter

        binding.btnSkip.setOnClickListener {
            navigateToAuth()
        }

        binding.btnContinue.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current + 1 < onboardingAdapter.itemCount) {
                binding.viewPager.currentItem = current + 1
            } else {
                navigateToAuth()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingAdapter.itemCount - 1) {
                    binding.btnContinue.text = "Get Started"
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnContinue.text = "Continue"
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun navigateToAuth() {
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        prefs.edit { putBoolean("onboarding_completed", true) }

        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun isOnboardingCompleted(): Boolean {
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        return prefs.getBoolean("onboarding_completed", false)
    }
}