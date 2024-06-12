package com.example.b09_wqga.database

data class Word(
    val headword: String = "",
    val lang: String = "",
    val meanings: List<String>,
    val right: Int = 0,
    val wrong: Int = 0,
    val create_date: String = ""
)
