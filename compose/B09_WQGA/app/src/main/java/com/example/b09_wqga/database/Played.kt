package com.example.b09_wqga.database

data class Played(
    val user_id: Int = 0,
    val game_id: Int = 0,
    val best_score: Int = 0,
    val right: Int = 0,
    val wrong: Int = 0,
    val play_count: Int = 0,
    val play_date: String = ""
) {
    // 기본 생성자 추가
    constructor() : this(0, 0, 0, 0, 0, 0, "")
}
