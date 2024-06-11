package com.example.b09_wqga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Attendance
import com.example.b09_wqga.repository.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val attendanceRepository: AttendanceRepository) : ViewModel() {

    fun addOrUpdateAttendance(attendance: Attendance, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = attendanceRepository.addOrUpdateAttendance(attendance)
            Log.d("AttendanceViewModel", "addOrUpdateAttendance result: $result")
            onComplete(result)
        }
    }
}
