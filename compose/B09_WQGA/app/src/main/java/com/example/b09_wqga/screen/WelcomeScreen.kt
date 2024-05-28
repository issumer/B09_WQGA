/*
임시로 넣어 놓은 화면 (나중에 삭제 가능)
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.model.AuthenticationViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(navController: NavHostController) {
    val authenticationViewModel: AuthenticationViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Welcome Screen",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "${authenticationViewModel.userID}님 환영합니다.",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold
        )

        LaunchedEffect(key1 = Unit) {
            delay(2000)
            authenticationViewModel.loginStatus.value = true
        }
        if(authenticationViewModel.loginStatus.value) {
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

            navController.navigate(Routes.MainScreen.route) {
                popUpTo(Routes.LoginScreen.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}
