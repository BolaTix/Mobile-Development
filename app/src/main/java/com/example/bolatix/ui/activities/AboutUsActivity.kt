package com.example.bolatix.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bolatix.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private var _binding: ActivityAboutUsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarAbout.toolbarTitle.text = "Tentang Kami"
        binding.topBarAbout.btnBack.visibility = View.VISIBLE
        binding.topBarAbout.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}