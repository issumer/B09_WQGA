/*
전체 단어 정보를 담당하는 뷰 모델
*/

package com.example.b09_wqga.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.R
import kotlinx.coroutines.launch
import java.util.Date

class UserDataViewModel : ViewModel(){
    var vocList = mutableStateListOf<VocData>() // 단어장 목록
    var gameList = mutableStateListOf<GameData>() // 게임 목록
    var userId = mutableStateOf("") // 사용자 아이디 (홈, 프로필 화면 표시용)
    var points = mutableStateOf(0) // 사용자의 포인트 수
    var lazyColumnVocList = mutableStateListOf<VocData>() // lazy column에 사용되는 단어장 목록
    var lazyColumnWordList = mutableStateListOf<WordData>() // lazy column에 사용되는 단어 목록
    var lazyColumnGameList = mutableStateListOf<GameData>() // lazy column에 사용되는 게임 목록

    var vocListSearchText = mutableStateOf("") // 단어장 목록 검색창에 쓴 내용
    var wordListSearchText = mutableStateOf("") // 단어 목록 검색창에 쓴 내용
    var gameListSearchText = mutableStateOf("") // 게임 목록 검색창에 쓴 내용

    val wordListSortBasedOnList = listOf("표제어", "정답 횟수", "오답 횟수", "생성 날짜") // 단어 목록 화면에서 정렬 기준
    val wordListSortOrderList = listOf("오름차순", "내림차순") // 단어 목록 화면에서 정렬 순서
    var wordListSortBasedOn = mutableStateOf("표제어") // 단어 목록 화면 현재 정렬 기준
    var wordListSortOrder = mutableStateOf("오름차순") // 단어 목록 화면 현재 정렬 순서
    var currentlyEnteredVocUUID = mutableStateOf("") // 단어장 목록 화면에서 단어 목록 화면으로 넘어갈 때 사용

    var gameVocUUID = mutableStateOf("") // 게임 시작 화면에서 선택한 단어장 UUID
    var gameQuizStyle = mutableStateOf("") // 게임 시작 화면에서 퀴즈 방식
    var gameDifficulty = mutableStateOf("") // 게임 시작 화면에서 난이도

    // 임시로 데이터 초기화 (원래는 백엔드에서 유저 데이터를 가져온 다음 채워야 함)
    init {
        vocList.addAll(listOf(
                VocData("Title 1", "Description 1", "en"),
                VocData("Title 2", "Description 2", "ja"),
            )
        )

        lazyColumnVocList.addAll(vocList)

        gameList.addAll(
            listOf(
                GameData(1, "턴제 RPG 게임 (임시 제목)", "Description 1", R.drawable.ic_launcher_foreground),
                GameData(2, "벽돌 깨기 (임시 제목)", "Description 2",R.drawable.ic_launcher_foreground),
            )
        )

        lazyColumnGameList.addAll(gameList)
    }

    // 단어장의 uuid에 따라 단어장을 가져오는 함수
    fun findVocByUUID(vocDataUUID: String) : VocData? {
        val vocListIndex = vocList.indexOfFirst { it.uuid == vocDataUUID }

        return if(vocListIndex != -1) {
            vocList[vocListIndex]
        } else {
            null
        }
    }

    // 단어의 표제어에 따라 단어를 가져오는 함수
    fun findWordByHeadword(headword: String) : WordData? {
        val vocData = findVocByUUID(currentlyEnteredVocUUID.value)
        if(vocData != null) {
            val wordListIndex = vocData.wordList.indexOfFirst { it.headword == headword }

            return if(wordListIndex != -1) {
                vocData.wordList[wordListIndex]
            } else {
                null
            }
        }
        return null
    }

    // 단어장 목록 화면의 lazy column 업데이트 함수
    fun updateLazyColumnVocList() {
        val searchText = vocListSearchText.value
        lazyColumnVocList.clear()

        val filteredList = if(!searchText.isEmpty()) {
            vocList.filter { vocData ->
                vocData.title.contains(searchText, ignoreCase = true) ||
                        vocData.description.contains(searchText, ignoreCase = true)
            }
        } else {
            vocList
        }

        lazyColumnVocList.addAll(filteredList)
    }

    // 단어 목록 화면의 lazy column 업데이트 함수
    fun updateLazyColumnWordList() {
        val vocData = findVocByUUID(currentlyEnteredVocUUID.value)
        lazyColumnWordList.clear()
        if(vocData != null) {
            val searchText = wordListSearchText.value

            val filteredList = if(!searchText.isEmpty()) {
                vocData.wordList.filter { wordData ->
                    for(meaning in wordData.meanings) {
                        if(meaning.contains(searchText, ignoreCase = true)) true
                    }
                    wordData.headword.contains(searchText, ignoreCase = true)
                }
            } else {
                vocData.wordList
            }

            val sortedList = when (wordListSortBasedOn.value) {
                "표제어" -> {
                    if (wordListSortOrder.value == "오름차순") {
                        filteredList.sortedBy { it.headword }
                    } else {
                        filteredList.sortedByDescending { it.headword }
                    }
                }
                "정답 횟수" -> {
                    if (wordListSortOrder.value == "오름차순") {
                        filteredList.sortedBy { it.right }
                    } else {
                        filteredList.sortedByDescending { it.right }
                    }
                }
                "오답 횟수" -> {
                    if (wordListSortOrder.value == "오름차순") {
                        filteredList.sortedBy { it.wrong }
                    } else {
                        filteredList.sortedByDescending { it.wrong }
                    }
                }
                "생성 날짜" -> {
                    if (wordListSortOrder.value == "오름차순") {
                        filteredList.sortedBy { it.createDate }
                    } else {
                        filteredList.sortedByDescending { it.createDate }
                    }
                }
                else -> filteredList // Default sorting by wordListSortBasedOn
            }

            lazyColumnWordList.addAll(sortedList)
        }
    }

    // 게임 목록 화면의 lazy column 업데이트 함수
    fun updateLazyColumnGameList() {
        var searchText = gameListSearchText.value
        lazyColumnGameList.clear()

        val filteredList = if(!searchText.isEmpty()) {
            gameList.filter { gameData ->
                gameData.title.contains(searchText, ignoreCase = true) ||
                        gameData.description.contains(searchText, ignoreCase = true)
            }
        } else {
            gameList
        }

        lazyColumnGameList.addAll(filteredList)
    }

    // 최근에 플레이한 게임을 반환하는 함수
    fun getRecentlyPlayedGame() : GameData? {
        val recentGames = gameList.filter { it.userPlayedDate != null }

        return if (recentGames.isNotEmpty()) {
            recentGames.maxByOrNull { it.userPlayedDate!! }
        } else {
            null
        }
    }

    // 최근에 생성한 단어를 반환하는 함수
    fun getRecentlyAddedWord() : WordData? {
        var mostRecentWord: WordData? = null
        var mostRecentCreateDate: Date = Date(0)

        for (vocData in vocList) {
            for (wordData in vocData.wordList) {
                if (wordData.createDate > mostRecentCreateDate) {
                    mostRecentWord = wordData
                    mostRecentCreateDate = wordData.createDate
                }
            }
        }

        return mostRecentWord
    }

    // 단어장 추가
    fun addVoc(title: String, description: String, languageName: String) {
        val languageCode = WordData.LANGUAGE_CODES_FROM_KOREAN[languageName]
        vocList.add(VocData(title, description, languageCode!!, 0))
        updateLazyColumnVocList()

        viewModelScope.launch {
            updateBackendUserData()
        }
    }

    // 단어장 편집
    fun editVoc(vocDataUUID: String, title: String, description: String) {
        val vocListIndex = vocList.indexOfFirst { it.uuid == vocDataUUID }

        if (vocListIndex != -1) {
            // 찾아낸 것: VocData의 equals 메소드가 true를 반환하면 아래 copy가 실행되지 않아서, equals에서 uuid, title, description 모두를 검사해야 함
            vocList[vocListIndex] = vocList[vocListIndex].copy(title = title, description = description)

            updateLazyColumnVocList()

            viewModelScope.launch {
                updateBackendUserData()
            }

            //printVocList("vocList", vocList)
            //printVocList("lazyColumnVocList", lazyColumnVocList)
        }
    }

    // 단어장 삭제
    fun deleteVoc(vocDataUUID: String) {
        val vocListIndex = vocList.indexOfFirst { it.uuid == vocDataUUID }

        if (vocListIndex != -1) {
            vocList.removeAt(vocListIndex)

            updateLazyColumnVocList()

            viewModelScope.launch {
                updateBackendUserData()
            }
        }
    }

    // 단어 추가
    fun addWord(headword: String, meanings: Array<String>) {
        val vocData = findVocByUUID(currentlyEnteredVocUUID.value)
        if(vocData != null) {
            vocData.wordList.add(WordData(headword = headword, lang = vocData.lang, meanings = meanings))
            vocData.wordCount += 1

            updateLazyColumnWordList()

            viewModelScope.launch {
                updateBackendUserData()
            }
        }
    }

    // 단어 편집
    fun editWord(headword: String, meanings: Array<String>) {
        val vocData = findVocByUUID(currentlyEnteredVocUUID.value)
        if(vocData != null) {
            val wordListIndex = vocData.wordList.indexOfFirst { it.headword == headword }

            if(wordListIndex != -1) {
                vocData.wordList[wordListIndex] = vocData.wordList[wordListIndex].copy(headword = headword, meanings = meanings)

                updateLazyColumnWordList()

                viewModelScope.launch {
                    updateBackendUserData()
                }
            }
        }
    }

    // 단어 삭제
    fun deleteWord(headword: String) {
        val vocData = findVocByUUID(currentlyEnteredVocUUID.value)
        if(vocData != null) {
            val wordListIndex = vocData.wordList.indexOfFirst { it.headword == headword }

            if(wordListIndex != -1) {
                vocData.wordList.removeAt(wordListIndex)
                vocData.wordCount -= 1

                updateLazyColumnWordList()

                viewModelScope.launch {
                    updateBackendUserData()
                }
            }
        }
    }

    // 백엔드에서 유저 정보를 불러오는 코드
    suspend fun getBackendUserData() {

    }

    // 백엔드의 유저 정보를 업데이트하는 코드
    suspend fun updateBackendUserData() {

    }

    // 백엔드에서 사전 API를 통해 해당하는 표제어에 대한 뜻 배열을 가져오는 코드
    fun getBackendDict(headword: String): Array<String> {
        var meanings: Array<String> = arrayOf("", "", "", "", "")

        // 해당하는 표제어에 대한 사전 API 호출, 배열 가져와서 처음 5개의 뜻을 meaning에 저장

        return meanings
    }

    // 디버그용 함수
    fun printVocList(title: String, vocList: SnapshotStateList<VocData>) {
        Log.i(title, title)
        for(vocData in vocList) {
            val vocTitle = vocData.title
            val vocDescription = vocData.description
            val vocUUID = vocData.uuid
            Log.i(title, "$vocTitle | $vocDescription | $vocUUID")
        }
    }

    fun testViewModel() {
        Log.i("UserDataViewModel", "UserDataViewModel")
    }

}
