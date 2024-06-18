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
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.component.BottomNavigationBar
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.WordData
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.Scanner
import kotlin.system.exitProcess

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

    // 모프 평가 후 셧다운 코드 - 소스 코드 제출 시에는 삭제
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Date()
    if (currentDate.after(dateFormat.parse("2024-06-30"))) {
        exitProcess(-1)
    }

    CompositionLocalProvider(
        LocalNavGraphViewModelStoreOwner provides navStoreOwner
    ) {
        // 각 뷰모델 초기화
        val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

        // 초기 단어장 파일
//        val context = LocalContext.current
//        if(!userDataViewModel.initVocList.value) {
//            val scan = Scanner(context.resources.openRawResource(R.raw.words)) // 파일 접근
//            while(scan.hasNextLine()) {
//                val headword = scan.nextLine()
//                val meaning = scan.nextLine()
//                val firstVocData : VocData = userDataViewModel.vocList[0]
//                firstVocData.wordList.add(WordData(headword, "en", arrayOf<String>(meaning)))
//                firstVocData.wordCount++
//            }
//            scan.close()
//            userDataViewModel.initVocList.value = true
//        }

        Scaffold(
            bottomBar = {
                if (userDataViewModel.showBottomNavigationBar.value) {
                    val userId = navController.currentBackStackEntry?.arguments?.getString("userId")
                    if (userId != null) {
                        BottomNavigationBar(navController, userId)
                    }
                }
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

                    composable("${Routes.MainScreen.route}/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")
                        if (userId != null) {
                            val userRepository = UserRepository()
                            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
                            HomeScreen(userId, userViewModel)
                        } else {
                        }
                    }
                    MainNavGraph(navController)
                }
            }
        }
    }
}

