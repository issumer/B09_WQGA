package com.example.b09_wqga.database


data class Voc(
    val user_id: Int = 0,
    val voc_id: Int = 0,
    val title: String = "",
    val description: String = "",
    val lang: String = "",
    val word_count: Int = 0,
    val words_json: List<Word> = emptyList(),
    val create_date: String = ""
) {
    // 기본 생성자 추가
    constructor() : this(0, 0, "", "", "", 0, emptyList(), "")
}
