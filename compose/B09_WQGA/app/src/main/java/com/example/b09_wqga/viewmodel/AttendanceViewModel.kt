package com.example.b09_wqga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.repository.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val attendanceRepository: AttendanceRepository) : ViewModel() {

    fun addAttendance(userId: Int, date: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = attendanceRepository.addAttendance(userId, date)
            Log.d("AttendanceViewModel", "addAttendance result: $result")
            onComplete(result)
        }
    }
}
