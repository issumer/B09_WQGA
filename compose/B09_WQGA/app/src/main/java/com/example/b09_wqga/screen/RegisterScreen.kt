/*
구현 목록에서 회원 가입 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.model.AuthenticationViewModel
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes

@Composable
fun RegisterScreen(navController: NavHostController) {
    val authenticationViewModel: AuthenticationViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var userID by rememberSaveable{
        mutableStateOf("")
    }
    var userPassword by rememberSaveable{
        mutableStateOf("")
    }
    var showRegisterFailDialog by rememberSaveable{
        mutableStateOf(false)
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text="Register",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = userID,
            onValueChange = {userID =it},
            label = {Text(stringResource(id = R.string.loginScreen_enterId))}
        )

        OutlinedTextField( value = userPassword,
            onValueChange = { userPassword = it },
            label = { Text(stringResource(id = R.string.loginScreen_enterPassword)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            authenticationViewModel.setUserInfo(userID, userPassword)
            // 로그인 백엔드 질의
            var registerResult = authenticationViewModel.checkRegister(userID, userPassword)

            if(registerResult) {
                LoginSuccess(authenticationViewModel, userDataViewModel, uiViewModel)
                navController.navigate(Routes.MainScreen.route) {
                    popUpTo(Routes.RegisterScreen.route) {
                        inclusive = true
                    }
                    popUpTo(Routes.InitialScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else {
                // 로그인 실패 메시지
                showRegisterFailDialog = true
            }

        }){
            Text(text = "회원가입")
        }

        if(showRegisterFailDialog) {
            RegisterFailDialog(
                {showRegisterFailDialog = false}
            )
        }
    }
}

@Composable
fun RegisterFailDialog(onConfirmClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "Register Failed", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Text(text = "Register Failed")
        },
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text(text = "OK")
            }
        }
    )
}