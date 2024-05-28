/*
로그인 관련 정보를 담당하는 뷰 모델
*/

package com.example.b09_wqga.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthenticationViewModel : ViewModel(){
    // 임시 아이디 & 패스워드
    val userIdDummy = "greenjoa"
    val userPasswordDummy = "1234"

    var userID = mutableStateOf("") // 사용자 아이디
    var userPassword = mutableStateOf("") // 사용자 비밀번호

    var loginStatus = mutableStateOf( false ) // 사용자 로그인 상태


    fun checkLogin(id:String, password:String):Boolean{
        // 로그인 체크하는 백엔드 코드 작성...

        // 임시 로그인 코드
        return userIdDummy == id && userPasswordDummy == password
    }

    fun checkRegister(id:String, password:String):Boolean{
        // 회원가입 체크하는 백엔드 코드 작성...

        // 임시 회원가입 코드
        return true
    }

    fun setUserInfo(id:String, password:String){
        userID.value = id
        userPassword.value = password
    }

    fun testViewModel() {
        Log.i("AuthenticationViewModel", "AuthenticationViewModel")
    }
}
