package com.example.b09_wqga.database

import java.util.UUID


data class Voc(
            val user_id: Int = 0,
            val voc_id: Int = 0,
            val uuid: String = UUID.randomUUID().toString(), // 단어장의 UUID
            val title: String = "",
            val description: String = "",
            val lang: String = "",
            val word_count: Int = 0,
            val words_json: List<Word> = emptyList(),
            val create_date: String = ""
        ) {
    constructor() : this(0, 0, "", "", "", "",0, emptyList(), "")
}
