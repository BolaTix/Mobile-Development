package com.example.bolatix.utils

import android.text.Editable
import android.text.TextWatcher
import com.example.bolatix.ui.components.CustomInput

fun CustomInput.doOnTextChanged(onTextChanged: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {}
    })
}
