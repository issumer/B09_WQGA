/*
전체 단어 정보를 담당하는 뷰 모델
*/

package com.example.b09_wqga.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.b09_wqga.R
import com.example.b09_wqga.database.Voc
import java.time.LocalDate

class UserDataViewModel : ViewModel(){
    //var vocList = mutableStateListOf<VocData>() // 단어장 목록
    var gameList = mutableStateListOf<GameData>() // 게임 목록
    var userID = mutableStateOf("") // 사용자 아이디 (navigation 용)
    //var lazyColumnVocList = mutableStateListOf<VocData>() // lazy column에 사용되는 단어장 목록
    var lazyColumnGameList = mutableStateListOf<GameData>() // lazy column에 사용되는 게임 목록

    var gameListSearchText = mutableStateOf("") // 게임 목록 검색창에 쓴 내용

    var gameVocUUID = mutableStateOf("") // 게임 시작 화면에서 선택한 단어장 UUID
    var gameQuizStyle = mutableStateOf(-1) // 게임 시작 화면에서 퀴즈 방식
    var gameDifficulty = mutableStateOf(-1) // 게임 시작 화면에서 난이도
    var currentQuiz = mutableStateListOf<Quiz>()

    // MainScreen의 Bottom Navigation Bar를 보여줄지 여부
    var showBottomNavigationBar = mutableStateOf(false)

    val currentDate = LocalDate.now()

    // 임시로 데이터 초기화 (원래는 백엔드에서 유저 데이터를 가져온 다음 채워야 함)
    init {
        //lazyColumnVocList.addAll(vocList)

        gameList.addAll(
            listOf(
                GameData(1, "턴제 RPG 게임", "Description 1", R.drawable.ic_launcher_foreground),
                GameData(2, "Quiz Brick", "Quiz 벽돌의 정답을 맞춰",R.drawable.ic_launcher_foreground),
            )
        )

        lazyColumnGameList.addAll(gameList)
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

    // 게임 시작을 위한 초기화
    fun gameInit(voc : Voc, quizStyle : Int, difficulty : Int) {
        gameQuizStyle.value = quizStyle
        gameDifficulty.value = difficulty

        if(currentQuiz.isEmpty()) {
            currentQuiz.add(Quiz(voc, quizStyle = quizStyle))
        } else {
            currentQuiz[0] = Quiz(voc, quizStyle = quizStyle)
        }
    }

    // 프로필 화면 total played games
    fun getTotalPlayedGames() : Int {
        var totalCount = 0
        gameList.forEach {
            totalCount += it.userPlayedCount
        }
        return totalCount
    }

    // 디버그용 함수
//    fun printVocList(title: String, vocList: SnapshotStateList<VocData>) {
//        Log.i(title, title)
//        for(vocData in vocList) {
//            val vocTitle = vocData.title
//            val vocDescription = vocData.description
//            val vocUUID = vocData.uuid
//            Log.i(title, "$vocTitle | $vocDescription | $vocUUID")
//        }
//    }

    fun testViewModel() {
        Log.i("UserDataViewModel", "UserDataViewModel")
    }

}
