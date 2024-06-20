/*
전체 단어 정보를 담당하는 뷰 모델
*/

package com.example.b09_wqga.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MiscViewModel : ViewModel(){
    var userID = mutableStateOf("") // 사용자 아이디 (navigation 용)
    var showBottomNavigationBar = mutableStateOf(false) // MainScreen의 Bottom Navigation Bar를 보여줄지 여부
    var quizStyle = mutableIntStateOf(-1) // 퀴즈 스타일
    var gameDifficulty = mutableIntStateOf(-1) // 게임 난이도

    fun testViewModel() {
        Log.i("UserDataViewModel", "UserDataViewModel")
    }

}
