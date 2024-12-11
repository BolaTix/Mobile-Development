package com.example.bolatix.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            topBarForgotPassword.btnBack.visibility = View.VISIBLE
            topBarForgotPassword.toolbarTitle.text = getString(R.string.lupa_sandi)
            topBarForgotPassword.btnBack.setOnClickListener { onBackPressed() }
            btnGmail.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:admin@bolatix.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Lupa Sandi Akun")
                    putExtra(Intent.EXTRA_TEXT, "Halo, saya lupa kata sandi akun saya.")
                }
                startActivity(Intent.createChooser(emailIntent, "Pilih Email Client"))
            }
        }
    }
}