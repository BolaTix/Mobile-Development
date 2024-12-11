package com.example.bolatix.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Tickets(
    @SerializedName("id")
    val id: Long,

    @SerializedName("date")
    val date: String,

    @SerializedName("time")
    val time: String,

    @SerializedName("stadion")
    val stadion: String,

    @SerializedName("homeTeamId")
    val homeTeamId: Int,

    @SerializedName("homeTeamName")
    val homeTeamName: String,

    @SerializedName("homeTeamLogo")
    val homeTeamLogo: String,

    @SerializedName("awayTeamId")
    val awayTeamId: Int,

    @SerializedName("awayTeamName")
    val awayTeamName: String,

    @SerializedName("awayTeamLogo")
    val awayTeamLogo: String,

    @SerializedName("slug")
    val slug: String? = null,
) : Parcelable
