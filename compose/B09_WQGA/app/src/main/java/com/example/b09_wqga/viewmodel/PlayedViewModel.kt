package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Played
import com.example.b09_wqga.repository.PlayedRepository
import kotlinx.coroutines.launch

class PlayedViewModel(private val playedRepository: PlayedRepository) : ViewModel() {

    fun addPlayed(played: Played, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = playedRepository.addPlayed(played)
            onComplete(result)
        }
    }

    fun getPlayed(user_id: Int, game_id: Int, onComplete: (Played?) -> Unit) {
        viewModelScope.launch {
            val played = playedRepository.getPlayed(user_id, game_id)
            onComplete(played)
        }
    }

    fun updatePlayed(played: Played, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = playedRepository.updatePlayed(played)
            onComplete(result)
        }
    }

    fun deletePlayed(user_id: Int, game_id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = playedRepository.deletePlayed(user_id, game_id)
            onComplete(result)
        }
    }

    fun getAllPlayedByUserId(user_id: Int, onComplete: (List<Played>) -> Unit) {
        viewModelScope.launch {
            val playedList = playedRepository.getAllPlayedByUserId(user_id)
            onComplete(playedList)
        }
    }

    fun getAllPlayedByGameId(game_id: Int, onComplete: (List<Played>) -> Unit) {
        viewModelScope.launch {
            val playedList = playedRepository.getAllPlayedByGameId(game_id)
            onComplete(playedList)
        }
    }
}
