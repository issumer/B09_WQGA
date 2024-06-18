/*
단어장의 각 단어를 나타내는 클래스
*/

package com.example.b09_wqga.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.database.Word
import java.util.Arrays
import java.util.Date
import java.util.Locale
import kotlin.random.Random

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
        val MAX_WORD_COUNT = 50 // 한 단어장 내 단어 개수 제한

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


data class Quiz(
    val voc: Voc,
    var currentQuiz : Int = -1, // 0(multiple choice), 1(short answer)
    var currentQuizType : Int = -1,
    var currentQuizNum : Int = 0,
    var currentAnswerCorrect : Int = 0, // 0(아직 퀴즈 풀지 않음), 1(정답), 2(오답)
    var quizStyle : Int = 0, // quizStyle - 0(완전 랜덤), 1(틀린 단어 위주), 2(객관식), 3(주관식)
    var wordPool : List<Word>? = null,
    var answerWord : Word? = null,
    var description: String = "",
    var choiceOptions: MutableList<String>? = null,
    var choiceOptionsWordData : MutableList<Word>? = null,
    var answerNums : MutableList<Int>? = null,
    var shortAnswer : String = "",
) {
    init {
        if(quizStyle == 1) { // 틀린 단어 위주일 경우
            val totalWrong = voc.words_json.sumOf { it.wrong }

            val wrongMean : Double = if (voc.words_json.isNotEmpty()) {
                totalWrong.toDouble() / voc.words_json.size
            } else {
                0.0
            }
            wordPool = voc.words_json.filter { it.wrong >= wrongMean }
        } else {
            wordPool = voc.words_json
        }

        // 첫번째 퀴즈 생성
        createQuiz()
    }

    fun createQuiz() {
        if(quizStyle == 2 || quizStyle == 3) {
            currentQuiz = quizStyle - 2
        } else {
            currentQuiz = Random.nextInt(2)
        }

        when(currentQuiz) {
            0 -> createMultipleChoiceQuiz()
            1 -> createShortAnswerQuiz()
        }
        currentQuizNum++
        currentAnswerCorrect = 0
    }
    fun createMultipleChoiceQuiz() {
        answerWord = voc.words_json.random(Random)
        choiceOptions = mutableListOf<String>()
        choiceOptionsWordData = mutableListOf<Word>()
        answerNums = mutableListOf<Int>()
        currentQuizType = Random.nextInt(2)

        // 답이 되는 단어의 뜻 중 ""이 아닌 뜻 찾기
        val answerNonEmptyMeanings = answerWord!!.meanings.filter { it.isNotEmpty() }
        val answerRandomMeaning = answerNonEmptyMeanings.random(Random)

        val randomWordPool = wordPool!!.shuffled(Random)

        when(currentQuizType) {
            0 -> { // 외국어 -> 한글
                description = "다음 중 \"${answerRandomMeaning}\"를 의미하는 단어는 무엇인지 고르시오."
                wordDataLoop@ for(wordData in randomWordPool) {
                    if(choiceOptionsWordData!!.size >= 3) break // 최대 3개의 틀린 선택지
                    for(meaning in wordData.meanings) {
                        if(meaning.isEmpty()) continue
                        if(meaning == answerRandomMeaning) continue@wordDataLoop
                    }
                    choiceOptionsWordData!!.add(wordData)
                }
                choiceOptionsWordData!!.add(answerWord!!) // 1개의 답 선택지 추가
                choiceOptionsWordData = choiceOptionsWordData!!.shuffled(Random).toMutableList()

                // 루프를 돌면서 선택지 중 정답 선택지를 찾아서 저장
                var choiceOptionsIndex = 0
                choiceOptionsLoop@ for(wordData in choiceOptionsWordData!!) {
                    for(meaning in wordData.meanings) {
                        if(meaning.isEmpty()) continue
                        if(meaning == answerRandomMeaning) {
                            answerNums!!.add(choiceOptionsIndex)
                            break@choiceOptionsLoop
                        }
                    }
                    choiceOptionsIndex++
                }
            }
            1 -> { // 한글 -> 외국어
                description = "다음 중 \"${answerWord!!.headword}\"의 한글 뜻을 모두 고르시오."
                for(wordData in randomWordPool) {
                    if(choiceOptions!!.size >= 3) break // 최대 3개의 랜덤 선택지
                    val nonEmptyMeanings = wordData.meanings.filter { it.isNotEmpty() }
                    choiceOptions!!.add(nonEmptyMeanings.random(Random))
                }
                choiceOptions!!.add(answerRandomMeaning) // 1개의 확정적인 답 선택지 추가
                choiceOptions = choiceOptions!!.shuffled(Random).toMutableList()

                // 루프를 돌면서 선택지 중 모든 정답 선택지를 찾아서 저장
                var choiceOptionsIndex = 0
                for(meaning in choiceOptions!!) {
                    for(answerMeaning in answerNonEmptyMeanings) {
                        if(meaning == answerMeaning) {
                            answerNums!!.add(choiceOptionsIndex)
                            break
                        }
                    }
                    choiceOptionsIndex++
                }
            }
        }
    }

    fun createShortAnswerQuiz() {
        answerWord = voc.words_json.random(Random)
        choiceOptionsWordData = mutableListOf<Word>()
        currentQuizType = Random.nextInt(3)

        // 답이 되는 단어의 뜻 중 ""이 아닌 뜻 찾기
        val answerNonEmptyMeanings = answerWord!!.meanings.filter { it.isNotEmpty() }
        val answerRandomMeaning = answerNonEmptyMeanings.random(Random)

        val cleanHeadword = answerWord!!.headword.replace(" ","")
        val randomAlphabet = cleanHeadword[Random.nextInt(cleanHeadword.length)]

        when(currentQuizType) {
            0 -> {
                description = "\"${answerRandomMeaning}\"을 의미하는 단어의 표제어를 적으시오."
                shortAnswer = answerWord!!.headword
            }
            1 -> {
                description = "\"${answerRandomMeaning}\"을 의미하는 단어에서 \'${randomAlphabet}\'의 개수를 적으시오."
                shortAnswer = "${answerWord!!.headword.count { it == randomAlphabet }}"
            }
            2 -> {
                description = "\"${answerRandomMeaning}\"을 의미하는 단어의 시작하는 알파벳을 적으시오."
                shortAnswer = "${answerWord!!.headword[0]}"
            }
        }
    }

    fun checkMultipleChoiceAnswer(answerSheet : MutableList<Int>) : Boolean {
        answerNums!!.sort()
        answerSheet.sort()
        val correct = answerNums!! == answerSheet // 두 리스트 내용물이 같은지 확인

        // 맞은 개수, 틀린 개수 업데이트
        if(correct) {
            //answerWord!!.right += 1 // Word 데이터 클래스에 맞게 코드 변화 필요
            currentAnswerCorrect = 1
        } else {
            // answerWord!!.wrong += 1 // Word 데이터 클래스에 맞게 코드 변화 필요
            currentAnswerCorrect = 2
        }

        return correct
    }

    fun checkShortAnswerAnswer(answerString : String) : Boolean {
        val correct = shortAnswer == answerString

        // 맞은 개수, 틀린 개수 업데이트
        if(correct) {
            //answerWord!!.right += 1 // Word 데이터 클래스에 맞게 코드 변화 필요
            currentAnswerCorrect = 1
        } else {
            //answerWord!!.wrong += 1 // Word 데이터 클래스에 맞게 코드 변화 필요
            currentAnswerCorrect = 2
        }

        return correct
    }
}


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