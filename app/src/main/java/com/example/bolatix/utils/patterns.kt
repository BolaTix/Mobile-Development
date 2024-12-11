package com.example.bolatix.utils

import com.example.bolatix.data.models.ValidationResultModels

fun emailPattern(email: String): ValidationResultModels {
    if (email.isBlank()) {
        return ValidationResultModels(false, "Email wajib diisi!")
    }

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    if (!email.matches(Regex(emailRegex))) {
        return ValidationResultModels(false, "Email tidak valid!")
    }

    return ValidationResultModels(true)
}

fun passwordPattern(password: String): ValidationResultModels {
    if (password.isBlank()) {
        return ValidationResultModels(false, "Password wajib diisi!")
    }

    val emailRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    if (!password.matches(Regex(emailRegex))) {
        return ValidationResultModels(false, "Kata sandi harus terdiri dari 8+ karakter, termasuk angka, huruf besar, huruf kecil, dan simbol.")
    }

    return ValidationResultModels(true)
}