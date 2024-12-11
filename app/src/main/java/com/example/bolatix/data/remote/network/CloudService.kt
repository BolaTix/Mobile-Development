package com.example.bolatix.data.remote.network

import com.example.bolatix.data.remote.response.AllMatchResponse
import com.example.bolatix.data.remote.response.ProfilePictureResponse
import com.example.bolatix.data.remote.response.RecommendedResponse
import com.example.bolatix.data.remote.response.StandingsResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface CloudService {

    @GET("recommend-teamfavorite")
    suspend fun getFavoriteByTeam(
        @Query("user_id") userId: String
    ): RecommendedResponse

    @GET("recommend-history")
    suspend fun getFavoriteByHistory(
        @Query("user_id") userId: String
    ): RecommendedResponse

    @GET("alldata")
    suspend fun getAllMatch(): AllMatchResponse

    @GET("standings")
    suspend fun getStandings(): StandingsResponse

    @Multipart
    @POST("users/{userId}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("userId") userId: String,
        @Part profilePicture: MultipartBody.Part
    ) : ProfilePictureResponse
}