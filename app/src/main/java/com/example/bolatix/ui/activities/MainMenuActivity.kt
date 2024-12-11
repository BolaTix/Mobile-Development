package com.example.bolatix.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivityMainMenuBinding
import com.example.bolatix.preference.UserPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var userPreferences: UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userPreferences.getUser().collect { user ->
                    if (user.favoriteTeam.isNullOrEmpty()) {
                        startActivity(Intent(this@MainMenuActivity, FavoriteActivity::class.java))
                        finish()
                        return@collect
                    }
                }
            }
        }
        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val navView: BottomNavigationView = binding.bottomNavigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navHome, R.id.navTicket, R.id.navStandings, R.id.navProfile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navHome -> {
                    setToolbar("Beranda")
                }
                R.id.navTicket -> {
                    setToolbar("Tiket Pertandingan")
                }
                R.id.navStandings -> {
                    setToolbar("Klasemen")
                }
                R.id.navProfile -> {
                    setToolbar("Akun Saya")
                }
            }
        }
    }

    private fun setToolbar(title: String) {
//        binding.topBar.visibility = if (title == "Akun Saya") View.GONE else View.VISIBLE
        binding.toolbarTitle.text = title
    }
}