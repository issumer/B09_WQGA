/*
단어장의 각 단어를 나타내는 클래스
*/

package com.example.b09_wqga.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.tooling.preview.Preview
import java.util.Arrays
import java.util.Date
import java.util.Locale
import java.util.UUID

// 단어 객체
data class WordData(
    var headword:String, // 표제어
    var lang:String, // 언어
    var meanings:Array<String>, // 뜻 (최대 5개)
    var right:Int = 0, // 퀴즈에서 이 단어에 대한 문제를 맞은 횟수
    var wrong:Int = 0, // 퀴즈에서 이 단어에 대한 문제를 틀린 횟수
    val createDate: Date = Date() // 단어 생성 날짜/시간
) {
    //constructor(headword:String, lang:String, meanings:Array<String>) :this(headword, lang, meanings, 0, 0)

    companion object {
        val MAX_MEANINGS = 5 // 최대 뜻 개수는 5개

        // 언어 코드: 언어명(한국어) 형태의 맵
        // 예: "en" to "영어"
        val LANGUAGE_NAMES_IN_KOREAN = mutableMapOf<String, String>().run {
            val availableLocales = Locale.getAvailableLocales()
            for (locale in availableLocales) {
                val languageCode = locale.language
                if(languageCode == "ko") continue // 한국어는 제외
                val languageNameInKorean = getLanguageName(languageCode)
                this[languageCode] = languageNameInKorean
            }
            this.toSortedMap(compareBy { this[it] })
        }

        // 언어명(한국어): 언어 코드 형태의 맵
        // 예: "영어" to "en"
        val LANGUAGE_CODES_FROM_KOREAN = LANGUAGE_NAMES_IN_KOREAN.entries.associate { (key, value) -> value to key }

        fun getLanguageName(languageCode: String): String {
            val locale = Locale(languageCode)
            val koreanLocale = Locale("ko", "KR") // Korean Locale
            return locale.getDisplayLanguage(koreanLocale)
        }
    }

    // headword, meanings 전체가 같으면 같은 단어
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WordData) return false

        return headword == other.headword && meanings.contentEquals(other.meanings)
    }

    override fun hashCode(): Int {
        var result = headword.hashCode()
        result = 31 * result + Arrays.hashCode(meanings)
        return result
    }
}

// 단어장 객체
data class VocData(
    val title: String, // 단어장 제목
    val description: String, // 단어장 설명
    val lang: String, // 단어장의 언어
    var wordCount: Int = 0, // 단어장의 단어 개수
    val wordList: MutableList<WordData> = mutableListOf(), // 단어장의 단어 리스트
    val uuid: String = UUID.randomUUID().toString(), // 단어장의 UUID
    val createDate: Date = Date() // 단어장의 생성 날짜
) {
    // uuid, title, description이 같으면 같은 단어장
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VocData) return false

        return uuid == other.uuid && title == other.title && description == other.description
    }

    override fun hashCode(): Int {
        return uuid.hashCode() + title.hashCode() + description.hashCode()
    }

}

// 게임 객체
data class GameData(
    val id: Int, // 게임 아이디
    val title: String, // 게임 제목
    val description: String, // 게임 설명
    val imageResource: Int, // 게임 이미지
    var userPlayedDate: Date? = null, // 사용자가 최근에 플레이한 날짜/시간
    var userPlayedCount: Int = 0, // 사용자가 총 플레이한 횟수
    var userRanking: Int = 1, // 사용자의 랭킹
    var userRight: Int = 0, // 사용자가 이 게임에서 총 문제를 맞힌 횟수
    var userWrong: Int = 0 // 사용자가 이 게임에서 총 문제를 틀린 횟수
)


@Preview
@Composable
fun LanguageCodePreview() {
    Column () {
        LazyColumn {
            items(WordData.LANGUAGE_NAMES_IN_KOREAN.toList()) {
                val languageCode = it.first
                val languageNameInKorean = it.second
                Text("$languageCode : $languageNameInKorean")
            }
        }
    }
}