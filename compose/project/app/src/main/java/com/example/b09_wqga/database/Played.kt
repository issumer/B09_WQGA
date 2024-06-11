package com.example.b09_wqga.database

data class Played(
    val user_id: Int,
    val game_id: Int,
    val best_score: Int = 0,
    val right: Int = 0,
    val wrong: Int = 0,
    val play_count: Int = 0,
    val play_date: String = ""
)
