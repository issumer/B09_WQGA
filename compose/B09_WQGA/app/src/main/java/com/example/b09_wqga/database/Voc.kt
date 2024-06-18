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
            // 기본 생성자 추가
//    constructor() : this(0, 0, "", "", "", "",0, emptyList(), "")
            companion object {
                val MAX_VOC_COUNT = 5 // 단어장 개수 제한
            }

            // uuid, title, description이 같으면 같은 단어장
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Voc) return false

        return uuid == other.uuid && title == other.title && description == other.description
    }

    override fun hashCode(): Int {
        return uuid.hashCode() + title.hashCode() + description.hashCode()
    }
}
