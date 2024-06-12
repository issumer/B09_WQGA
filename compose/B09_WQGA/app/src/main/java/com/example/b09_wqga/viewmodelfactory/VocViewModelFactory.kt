package com.example.b09_wqga.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.viewmodel.VocViewModel

class VocViewModelFactory(private val vocRepository: VocRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VocViewModel(vocRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
