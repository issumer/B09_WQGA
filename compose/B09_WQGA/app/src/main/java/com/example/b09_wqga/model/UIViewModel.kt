/*
UI 관련 State 정보를 담당하는 뷰 모델
*/

package com.example.b09_wqga.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UIViewModel : ViewModel(){
    // MainScreen의 Bottom Navigation Bar를 보여줄지 여부
    var showBottomNavigationBar = mutableStateOf(false)
}
