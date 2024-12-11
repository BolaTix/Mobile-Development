package com.example.bolatix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolatix.data.remote.response.ProfilePictureResponse
import com.example.bolatix.data.repository.FootballRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val repository: FootballRepository) : ViewModel() {

    private val _uploadStatus = MutableLiveData<Result<ProfilePictureResponse>>()
    val uploadStatus: LiveData<Result<ProfilePictureResponse>> get() = _uploadStatus

    fun uploadProfilePicture(userId: String, imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val result = repository.uploadProfilePicture(userId, imagePart)
                _uploadStatus.postValue(result)
            } catch (e: Exception) {
                _uploadStatus.postValue(Result.failure(e))
            }
        }
    }
}
