package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Attendance
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("attendances")

    suspend fun addOrUpdateAttendance(attendance: Attendance): Boolean {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(attendance.user_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key
            if (key != null) {
                database.child(key).setValue(attendance).await()
                Log.d("AttendanceRepository", "Attendance updated: $attendance")
            } else {
                val newId = database.push().key ?: return false
                database.child(newId).setValue(attendance).await()
                Log.d("AttendanceRepository", "Attendance added: $attendance")
            }
            true
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Error adding or updating attendance", e)
            false
        }
    }
}
