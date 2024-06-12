package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.repository.VocRepository
import kotlinx.coroutines.launch

class VocViewModel(private val vocRepository: VocRepository) : ViewModel() {

    fun addVoc(voc: Voc, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vocRepository.addVoc(voc)
            onComplete(result)
        }
    }

    fun getVoc(voc_id: Int, onComplete: (Voc?) -> Unit) {
        viewModelScope.launch {
            val voc = vocRepository.getVoc(voc_id)
            onComplete(voc)
        }
    }

    fun updateVoc(voc: Voc, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vocRepository.updateVoc(voc)
            onComplete(result)
        }
    }

    fun deleteVoc(voc_id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vocRepository.deleteVoc(voc_id)
            onComplete(result)
        }
    }

    fun getAllVocsByUserId(user_id: Int, onComplete: (List<Voc>) -> Unit) {
        viewModelScope.launch {
            val vocs = vocRepository.getAllVocsByUserId(user_id)
            onComplete(vocs)
        }
    }
}
