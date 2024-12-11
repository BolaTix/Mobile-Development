package com.example.bolatix.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class AllMatchResponse(
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<DataALlMatch>
)

@Parcelize
class DataALlMatch(
    @SerializedName("id_match")
    val idMatch: String,

    @SerializedName("home_team")
    val homeTeam: String,

    @SerializedName("away_team")
    val awayTeam: String,

    @SerializedName("home_score")
    val scoreHome: String = "",

    @SerializedName("away_score")
    val scoreAway: String = "",

    @SerializedName("stadion")
    val stadion: String,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("jam")
    val jam: String
) : Parcelable