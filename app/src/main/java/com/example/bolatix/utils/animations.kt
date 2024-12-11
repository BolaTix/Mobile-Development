package com.example.bolatix.utils

import android.app.Activity
import com.example.bolatix.R

@Suppress("DEPRECATION")
fun Activity.fadeInOut() {
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}