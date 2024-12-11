package com.example.bolatix.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bolatix.R
import com.example.bolatix.databinding.ActivitySignUpBinding
import com.example.bolatix.ui.viewmodels.AuthViewModel
import com.example.bolatix.utils.doOnTextChanged
import com.example.bolatix.utils.emailPattern
import com.example.bolatix.utils.passwordPattern
import com.example.bolatix.utils.showToast
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModel()

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupObservers()
    }

    private fun setupAction() {
        with(binding) {
            bindProgressButton(btnSignUp)

            btnToSignIn.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                finish()

            }

            etEmail.doOnTextChanged { checkEmailValidation(it) }

            etName.doOnTextChanged { checkNameValidation(it) }

            etPassword.doOnTextChanged { checkPasswordValidation(it) }

            etConPassword.doOnTextChanged { checkConfirmPasswordValidation(it) }

            btnSignUp.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                val confirmPassword = etConPassword.text.toString()
                val emailValidation = checkEmailValidation(email)
                val passwordValidation = checkPasswordValidation(password)
                val confirmPasswordValidation = checkConfirmPasswordValidation(confirmPassword)

                if (name.isBlank()) {
                    showMessageInput(4, "Nama wajib diisi!")
                    return@setOnClickListener
                }

                if (emailValidation && passwordValidation && confirmPasswordValidation) {
                    viewModel.register(name, email, password)
                    btnSignUp.showProgress {
                        R.string.signup
                        progressColor = Color.WHITE
                    }
                    btnSignUp.isEnabled = false
                }
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { result ->
                result?.onSuccess {
                    showToast(this@SignUpActivity, "Pembuatan Akun Berhasil")
                    startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                    finish()
                }?.onFailure { error ->
                    if (error is FirebaseAuthUserCollisionException) {
                        showToast(this@SignUpActivity, "Email sudah terdaftar, silakan gunakan email lain.")
                    } else {
                        showToast(this@SignUpActivity, error.message ?: "Terjadi kesalahan")
                    }
                }
                binding.btnSignUp.hideProgress(R.string.signup)
                binding.btnSignUp.isEnabled = true
            }
        }
    }

    private fun checkNameValidation(name: String) {
        if (name.isBlank()) {
            showMessageInput(4, "Nama wajib diisi!")
        } else {
            unshowMessageInput(4)
        }
    }

    private fun checkEmailValidation(email: String? = null): Boolean {
        val inputEmail = binding.etEmail.text.toString()
        val checkEmail =
            if (email.isNullOrBlank()) emailPattern(inputEmail) else emailPattern(email)
        when (checkEmail.status) {
            false -> showMessageInput(1, checkEmail.message.toString())
            true -> unshowMessageInput(1)
        }
        return checkEmail.status
    }

    private fun checkPasswordValidation(password: String? = null): Boolean {
        val inputPassword = binding.etPassword.text.toString()
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

    private fun checkConfirmPasswordValidation(confirmPassword: String): Boolean {
        val password = binding.etPassword.text.toString()
        if (confirmPassword.isBlank()) {
            showMessageInput(3, "Konfimasi password wajib diisi!")
            return false
        } else if (confirmPassword != password) {
            showMessageInput(3, "Password tidak cocok")
            return false
        } else {
            unshowMessageInput(3)
            return true
        }
    }

    private fun showMessageInput(type: Int, message: String) {
        with(binding) {
            when (type) {
                1 -> {
                    messErrEmail.text = message
                    messErrEmail.visibility = View.VISIBLE
                }

                2 -> {
                    messErrPassword.text = message
                    messErrPassword.visibility = View.VISIBLE
                }

                3 -> {
                    messErrConPassword.text = message
                    messErrConPassword.visibility = View.VISIBLE
                }

                4 -> {
                    messErrName.text = message
                    messErrName.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun unshowMessageInput(type: Int) {
        when (type) {
            1 -> binding.messErrEmail.visibility = View.GONE
            2 -> binding.messErrPassword.visibility = View.GONE
            3 -> binding.messErrConPassword.visibility = View.GONE
            4 -> binding.messErrName.visibility = View.GONE
        }
    }
}