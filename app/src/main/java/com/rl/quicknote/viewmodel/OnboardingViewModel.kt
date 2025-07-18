package com.rl.quicknote.viewmodel

import androidx.lifecycle.ViewModel
import com.rl.quicknote.R
import com.rl.quicknote.model.entities.OnboardingItem

class OnboardingViewModel: ViewModel() {

    val onboardingItems = listOf(
        OnboardingItem("Welcome to QuickNote", "Your personal space to capture ideas, tasks, and inspirations.", R.drawable.ic_screen1),
        OnboardingItem("Sync Across All Devices", "Your notes are securely stored and automatically synced in real-time using Firebase — so you can access them anytime, anywhere.", R.drawable.ic_screen2),
        OnboardingItem("Organize & Personalize", "Categorize, pin, and search notes easily. Switch between light and dark themes to match your style.", R.drawable.ic_screen3)
    )
}