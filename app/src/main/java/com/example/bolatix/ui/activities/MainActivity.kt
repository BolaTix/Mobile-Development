package com.example.bolatix.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.bolatix.R
import com.example.bolatix.preference.OnBoardingPreference
import com.example.bolatix.utils.fadeInOut
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var onBoardingStatus: OnBoardingPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        onBoardingStatus = OnBoardingPreference(this)
        Handler(Looper.getMainLooper()).postDelayed({
            nextPage()
            finish()
        }, 1500)
    }

    private fun nextPage() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val onboardingStatus = onBoardingStatus.isOnboardingCompleted()
        val page = if (onboardingStatus) {
            if (currentUser != null) {
                Intent(this, MainMenuActivity::class.java)
            } else {
                Intent(this, SignInActivity::class.java)
            }
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        startActivity(page)
        fadeInOut()
        finish()
    }
}