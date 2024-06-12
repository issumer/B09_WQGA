package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Game
import com.example.b09_wqga.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    fun addGame(game: Game, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.addGame(game)
            onComplete(result)
        }
    }

    fun getGame(game_id: Int, onComplete: (Game?) -> Unit) {
        viewModelScope.launch {
            val game = gameRepository.getGame(game_id)
            onComplete(game)
        }
    }

    fun updateGame(game: Game, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.updateGame(game)
            onComplete(result)
        }
    }

    fun deleteGame(game_id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = gameRepository.deleteGame(game_id)
            onComplete(result)
        }
    }

    fun getAllGames(onComplete: (List<Game>) -> Unit) {
        viewModelScope.launch {
            val games = gameRepository.getAllGames()
            onComplete(games)
        }
    }
}
