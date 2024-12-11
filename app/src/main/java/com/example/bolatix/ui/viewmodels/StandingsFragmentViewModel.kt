package com.example.bolatix.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bolatix.data.remote.response.Standings
import com.example.bolatix.data.repository.FootballRepository
import kotlinx.coroutines.launch

class StandingsFragmentViewModel(private val repository: FootballRepository) : ViewModel() {

    private val _standings = MutableLiveData<Result<List<Standings>>>()
    val standings: LiveData<Result<List<Standings>>> = _standings

    fun getStandings() {
        viewModelScope.launch {
            val result = repository.getStandings()
            _standings.postValue(result)
        }
    }
}