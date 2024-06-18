package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Played
import com.example.b09_wqga.repository.PlayedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayedViewModel(private val playedRepository: PlayedRepository) : ViewModel() {

    private val _playedList = MutableStateFlow<List<Played>>(emptyList())
    val playedList: StateFlow<List<Played>> = _playedList

    fun loadPlayedByUserId(userId: Int) {
        viewModelScope.launch {
            val playedList = playedRepository.getPlayedByUserId(userId)
            _playedList.value = playedList
        }
    }

    fun getAllPlayedByUserId(userId: Int, onComplete: (List<Played>) -> Unit) {
        viewModelScope.launch {
            val playedList = playedRepository.getPlayedByUserId(userId)
            onComplete(playedList)
        }
    }

    fun getTotalPlayedGames(): Int {
        return _playedList.value.size
    }
}
