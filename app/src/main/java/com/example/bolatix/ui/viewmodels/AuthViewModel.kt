package com.example.bolatix.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolatix.data.models.User
import com.example.bolatix.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<Result<User>?>(null)
    val authState: StateFlow<Result<User>?> get() = _authState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = authRepository.registerUser(name, email, password)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = authRepository.loginUser(email, password)
        }
    }

    suspend fun logout() {
        authRepository.logoutUser()
    }
}