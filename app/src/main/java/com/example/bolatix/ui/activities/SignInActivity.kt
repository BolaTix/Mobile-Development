package com.example.bolatix.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivitySignInBinding
import com.example.bolatix.preference.UserPreferences
import com.example.bolatix.ui.viewmodels.AuthViewModel
import com.example.bolatix.utils.doOnTextChanged
import com.example.bolatix.utils.emailPattern
import com.example.bolatix.utils.passwordPattern
import com.example.bolatix.utils.showToast
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthViewModel by viewModel()
    private lateinit var userPreferences: UserPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        setupAction()
        setupObservers()
    }

    private fun setupAction() {
        with(binding) {
            bindProgressButton(btnSignIn)
            btnForgotPassword.setOnClickListener {
                startActivity(Intent(this@SignInActivity, ForgotPasswordActivity::class.java))
            }
            btnToSignUp.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                finish()
            }
            inputEmail.doOnTextChanged { checkEmailValidation(it) }
            inputPassword.doOnTextChanged { checkPasswordValidation(it) }
            btnSignIn.setOnClickListener {
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()
                val emailValidation = checkEmailValidation(email)
                val passwordValidation = checkPasswordValidation(password)
                if (emailValidation && passwordValidation) {
                    viewModel.login(email, password)
                    btnSignIn.showProgress { progressColor = Color.WHITE }
                }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { result ->
                result?.onSuccess {
                    startActivity(Intent(this@SignInActivity, MainMenuActivity::class.java))
                    finish()
                }?.onFailure { error ->
                    println(error)
                    when (error) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            showToast(this@SignInActivity, "Email atau Password Salah!")
                        }
                        else -> {
                            showToast(this@SignInActivity, error.message ?: "Terjadi kesalahan")
                        }
                    }
                }
                binding.btnSignIn.hideProgress(R.string.signin)
                binding.btnSignIn.isEnabled = true
            }
        }
    }

    private fun checkEmailValidation(email: String? = null): Boolean {
        val inputEmail = binding.inputEmail.text.toString()
        val checkEmail =
            if (email.isNullOrBlank()) emailPattern(inputEmail) else emailPattern(email)
        when (checkEmail.status) {
            false -> showMessageInput(1, checkEmail.message.toString())
            true -> unshowMessageInput(1)
        }
        return checkEmail.status
    }

    private fun checkPasswordValidation(password: String? = null): Boolean {
        val inputPassword = binding.inputPassword.text.toString()
        val checkPassword =
            if (password.isNullOrBlank()) passwordPattern(inputPassword) else passwordPattern(
                password
            )
        when (checkPassword.status) {
            false -> showMessageInput(2, checkPassword.message.toString())
            true -> unshowMessageInput(2)
        }
        return checkPassword.status
    }

    private fun showMessageInput(type: Int, message: String) {
        if (type == 1) {
            binding.messageErrorEmail.text = message
            binding.messageErrorEmail.visibility = View.VISIBLE
        } else {
            binding.messageErrorPassword.text = message
            binding.messageErrorPassword.visibility = View.VISIBLE
        }
    }

    private fun unshowMessageInput(type: Int) {
        when (type) {
            1 -> binding.messageErrorEmail.visibility = View.GONE
            2 -> binding.messageErrorPassword.visibility = View.GONE
        }
    }
}