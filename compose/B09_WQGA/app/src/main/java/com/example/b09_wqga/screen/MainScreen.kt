/*
구현 목록에서 메인 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.b09_wqga.R
import com.example.b09_wqga.navigation.MainNavGraph
import com.example.b09_wqga.model.AuthenticationViewModel
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.component.BottomNavigationBar
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.VocData
import com.example.b09_wqga.model.WordData
import java.util.Scanner

@Composable
fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val context = LocalContext.current
    return remember(context) { context as ViewModelStoreOwner }
}

val LocalNavGraphViewModelStoreOwner =
    staticCompositionLocalOf<ViewModelStoreOwner> {
        error("Undefined")
    }

@Composable
fun MainScreen(navController: NavHostController) {
    val navStoreOwner = rememberViewModelStoreOwner()

    CompositionLocalProvider(
        LocalNavGraphViewModelStoreOwner provides navStoreOwner
    ) {
        // 각 뷰모델 초기화
        val authenticationViewModel: AuthenticationViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
        val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
        val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

        // 초기 단어장 파일
        val context = LocalContext.current
        if(!userDataViewModel.initVocList.value) {
            val scan = Scanner(context.resources.openRawResource(R.raw.words)) // 파일 접근
            while(scan.hasNextLine()) {
                val headword = scan.nextLine()
                val meaning = scan.nextLine()
                val firstVocData : VocData = userDataViewModel.vocList[0]
                firstVocData.wordList.add(WordData(headword, "en", arrayOf<String>(meaning)))
                firstVocData.wordCount++
            }
            scan.close()
            userDataViewModel.initVocList.value = true
        }


        Scaffold(
            bottomBar = {
                if (uiViewModel.showBottomNavigationBar.value)
                    BottomNavigationBar(navController)
            }
        ) { contentPadding ->

            Column(modifier = Modifier.padding(contentPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.InitialScreen.route
                ) {
                    composable(Routes.InitialScreen.route) {
                        InitialScreen(navController)
                    }

                    composable(Routes.LoginScreen.route) {
                        LoginScreen(navController)
                    }

                    composable(Routes.WelcomeScreen.route) {
                        WelcomeScreen(navController)
                    }

                    composable(Routes.RegisterScreen.route) {
                        RegisterScreen(navController)
                    }
                    // 중첩 NavGraph
                    MainNavGraph(navController)
                }
            }
        }
    }
}