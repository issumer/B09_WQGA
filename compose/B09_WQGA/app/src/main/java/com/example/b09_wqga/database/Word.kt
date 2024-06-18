package com.example.b09_wqga.database

data class Word(
    val word_id: Int = 0,
    val voc_id: Int = 0, // voc_id 필드 추가
    val headword: String = "",
    val lang: String = "",
    val meanings: List<String> = emptyList(),
    val right: Int = 0,
    val wrong: Int = 0,
    val create_date: String = ""
) {
    // 기본 생성자 추가
    constructor() : this(0, 0, "", "", emptyList(), 0, 0, "")
}
