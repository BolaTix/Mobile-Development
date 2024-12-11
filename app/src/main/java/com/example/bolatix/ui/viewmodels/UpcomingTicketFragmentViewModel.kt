package com.example.bolatix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolatix.data.remote.response.DataALlMatch
import com.example.bolatix.data.repository.FootballRepository
import kotlinx.coroutines.launch

class UpcomingTicketFragmentViewModel(private val repository: FootballRepository) : ViewModel() {

    private val _allMatch = MutableLiveData<Result<List<DataALlMatch>>>()
    val upTickets: LiveData<Result<List<DataALlMatch>>> = _allMatch

    fun getAllMatch() {
        viewModelScope.launch {
            val result = repository.getAllMatch()
            _allMatch.postValue(result)
        }
    }
}