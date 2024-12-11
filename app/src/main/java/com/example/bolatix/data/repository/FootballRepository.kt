package com.example.bolatix.data.repository

import com.example.bolatix.data.remote.network.CloudService
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.data.remote.response.DataRecomendded
import com.example.bolatix.data.remote.response.ProfilePictureResponse
import com.example.bolatix.data.remote.response.Standings
import okhttp3.MultipartBody

class FootballRepository(
    private val cloudService: CloudService
) {
    suspend fun getStandings(): Result<List<Standings>> {
        return try {
            val response = cloudService.getStandings()
            if (response.status) {
                Result.success(response.standings)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMatch(): Result<List<DataALlMatch>> {
        return try {
            val response = cloudService.getAllMatch()
            if (response.status) {
                Result.success(response.data)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteByTeam(userId: String): Result<List<DataRecomendded>> {
        return try {
            val response = cloudService.getFavoriteByTeam(userId)
            if (response.status) {
                Result.success(response.data)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteByHistory(userId: String): Result<List<DataRecomendded>> {
        return try {
            val response = cloudService.getFavoriteByHistory(userId)
            if (response.status) {
                Result.success(response.data)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePicture(userId: String, imagePart: MultipartBody.Part): Result<ProfilePictureResponse> {
        return try {
            val response = cloudService.uploadProfilePicture(userId, imagePart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}