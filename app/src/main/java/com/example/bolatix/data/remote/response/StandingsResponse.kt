package com.example.bolatix.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StandingsResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val standings: List<Standings>
)

@Parcelize
data class Standings(
    @SerializedName("posisi")
    val posisi: String,

    @SerializedName("namaTim")
    val namaTim: String,

    @SerializedName("logo")
    val logo: String,

    @SerializedName("main")
    val main: String,

    @SerializedName("poin")
    val poin: String,

    @SerializedName("menang")
    val menang: String,

    @SerializedName("seri")
    val seri: String,

    @SerializedName("kalah")
    val kalah: String,

    @SerializedName("goal")
    val goal: String,

    @SerializedName("selisihGoal")
    val selisihGoal: String,
) : Parcelable