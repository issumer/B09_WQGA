package com.example.b09_wqga.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.viewmodel.AttendanceViewModel

class AttendanceViewModelFactory(private val attendanceRepository: AttendanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(attendanceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
