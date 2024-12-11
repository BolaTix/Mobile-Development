package com.example.bolatix.data.remote.response

data class ProfilePictureResponse(
    val data: Data,
    val message: String,
    val status: Boolean
)

data class Data(
    val profile_picture_url: String
)
