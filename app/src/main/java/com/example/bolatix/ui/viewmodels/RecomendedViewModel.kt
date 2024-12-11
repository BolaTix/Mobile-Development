package com.example.bolatix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolatix.data.remote.response.DataRecomendded
import com.example.bolatix.data.repository.FootballRepository
import kotlinx.coroutines.launch

class RecomendedViewModel(private val repository: FootballRepository) : ViewModel() {

    private val _favoriteByTeam = MutableLiveData<Result<List<DataRecomendded>>>()
    val favoriteByTeam: LiveData<Result<List<DataRecomendded>>> = _favoriteByTeam

    private val _favoriteByHistory = MutableLiveData<Result<List<DataRecomendded>>>()
    val favoriteByHistory: LiveData<Result<List<DataRecomendded>>> = _favoriteByHistory

    fun getFavoriteByTeam(userId: String) {
        viewModelScope.launch {
            val result = repository.getFavoriteByTeam(userId)
            _favoriteByTeam.postValue(result)
        }
    }

    fun getFavoriteByHistory(userId: String) {
        viewModelScope.launch {
            val result = repository.getFavoriteByHistory(userId)
            _favoriteByHistory.postValue(result)
        }
    }
}