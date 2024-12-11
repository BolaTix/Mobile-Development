package com.example.bolatix.data.remote.response

import com.google.gson.annotations.SerializedName

class RecommendedResponse (
    @SerializedName("status")
    val status: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<DataRecomendded>
)

class DataRecomendded(
    @SerializedName("id_match")
    val matchId: String,

    @SerializedName("away_team")
    val awayTeam: String,

    @SerializedName("home_team")
    val homeTeam: String,

    @SerializedName("jam")
    val time: String,

    @SerializedName("stadion")
    val stadion: String,

    @SerializedName("tanggal")
    val tanggal: String
)