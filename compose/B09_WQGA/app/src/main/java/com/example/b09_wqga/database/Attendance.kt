package com.example.b09_wqga.database

data class Attendance(
    val user_id: Int = 0,
    val attendance_dates: List<String> = listOf()
)
