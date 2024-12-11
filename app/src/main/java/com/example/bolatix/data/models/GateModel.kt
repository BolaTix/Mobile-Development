package com.example.bolatix.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class GateModel (
    val id: Int,
    val title: String,
    val price: Int
) : Parcelable