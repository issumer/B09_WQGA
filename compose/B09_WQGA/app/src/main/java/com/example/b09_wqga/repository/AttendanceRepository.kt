package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Attendance
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("attendances")

    suspend fun addAttendance(userId: Int, date: String): Boolean {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(userId.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key
            if (key != null) {
                val existingAttendance = snapshot.children.firstOrNull()?.getValue(Attendance::class.java)
                val updatedDates = existingAttendance?.attendance_dates?.toMutableList() ?: mutableListOf()
                if (updatedDates.contains(date)) {
                    // 이미 같은 날에 로그인한 경우
                    Log.d("AttendanceRepository", "Duplicate login date: $date")
                    return true // 중복이여도 true 반환
                }
                updatedDates.add(date)
                val updatedAttendance = existingAttendance?.copy(attendance_dates = updatedDates)
                if (updatedAttendance != null) {
                    database.child(key).setValue(updatedAttendance).await()
                    Log.d("AttendanceRepository", "Attendance updated: $updatedAttendance")
                }
            } else {
                val newAttendance = Attendance(user_id = userId, attendance_dates = listOf(date))
                val newId = database.push().key ?: return false
                database.child(newId).setValue(newAttendance).await()
                Log.d("AttendanceRepository", "Attendance added: $newAttendance")
            }
            true
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Error adding attendance", e)
            false
        }
    }

    suspend fun getAttendanceDates(userId: Int): List<String> {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(userId.toDouble()).get().await()
            val attendance = snapshot.children.firstOrNull()?.getValue(Attendance::class.java)
            attendance?.attendance_dates ?: emptyList()
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Error fetching attendance dates", e)
            emptyList()
        }
    }
}
