package com.example.b09_wqga.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.b09_wqga.repository.PlayedRepository
import com.example.b09_wqga.viewmodel.PlayedViewModel

class PlayedViewModelFactory(private val playedRepository: PlayedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayedViewModel(playedRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
