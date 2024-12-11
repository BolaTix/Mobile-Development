package com.example.bolatix.utils

import com.example.bolatix.data.models.ValidationResultModels

fun emailPattern(email: String): ValidationResultModels {
    if (email.isBlank()) {
        return ValidationResultModels(false, "Email Cannot be Empty")
    }

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    if (!email.matches(Regex(emailRegex))) {
        return ValidationResultModels(false, "Invalid Email")
    }

    return ValidationResultModels(true)
}

fun passwordPattern(password: String): ValidationResultModels {
    if (password.isBlank()) {
        return ValidationResultModels(false, "Password Cannot be Empty")
    }

    val emailRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    if (!password.matches(Regex(emailRegex))) {
        return ValidationResultModels(false, "Password must be 8+ characters, include a number, uppercase, lowercase, and a symbol.")
    }

    return ValidationResultModels(true)
}