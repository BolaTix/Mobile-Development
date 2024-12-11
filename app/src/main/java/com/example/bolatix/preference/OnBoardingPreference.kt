package com.example.bolatix.preference

import android.content.Context
import android.content.SharedPreferences

class OnBoardingPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
    private val IS_ONBOARDING_COMPLETED = "isOnboardingCompleted"
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(IS_ONBOARDING_COMPLETED, false)
    }
    fun setOnboardingCompleted(isCompleted: Boolean) {
        sharedPreferences.edit().putBoolean(IS_ONBOARDING_COMPLETED, isCompleted).apply()
    }
}