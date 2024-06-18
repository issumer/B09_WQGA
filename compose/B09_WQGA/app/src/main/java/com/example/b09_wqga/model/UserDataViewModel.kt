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
    var userID = mutableStateOf("") // 사용자 아이디 (navigation 용)
    var showBottomNavigationBar = mutableStateOf(false) // MainScreen의 Bottom Navigation Bar를 보여줄지 여부

    fun testViewModel() {
        Log.i("UserDataViewModel", "UserDataViewModel")
    }

}
