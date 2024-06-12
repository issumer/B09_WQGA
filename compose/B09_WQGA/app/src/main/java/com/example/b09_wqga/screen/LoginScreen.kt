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
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodel.AttendanceViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import com.example.b09_wqga.viewmodelfactory.AttendanceViewModelFactory
import com.example.b09_wqga.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LoginScreen(navController: NavHostController) {
    val userRepository = UserRepository()
    val attendanceRepository = AttendanceRepository()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceRepository))
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var userID by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var showLoginFailDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userID,
            onValueChange = { userID = it },
            label = { Text(stringResource(id = R.string.loginScreen_enterId)) }
        )

        OutlinedTextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            label = { Text(stringResource(id = R.string.loginScreen_enterPassword)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            // 로그인 체크 코드
            if(userID.isEmpty() || userPassword.isEmpty()) {
                showLoginFailDialog = true
            } else {
                userViewModel.loginUser(userID, userPassword) { user ->
                    if (user != null) {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        attendanceViewModel.addAttendance(user.user_id, currentDate) { success ->
                            uiViewModel.showBottomNavigationBar.value = true
                            navigateToMainScreen(navController)
                        }
                    } else {
                        showLoginFailDialog = true
                    }
                }
            }
        }) {
            Text(text = "로그인")
        }

        if (showLoginFailDialog) {
            LoginFailDialog { showLoginFailDialog = false }
        }
    }
}

private fun navigateToMainScreen(navController: NavHostController) {
    navController.navigate(Routes.MainScreen.route) {
        popUpTo(Routes.LoginScreen.route) {
            inclusive = true
        }
        popUpTo(Routes.InitialScreen.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

@Composable
fun LoginFailDialog(onConfirmClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "로그인 실패", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = { Text(text = "로그인에 실패하였습니다. 아이디, 비번을 확인해주세요!") },
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text(text = "OK")
            }
        }
    )
}
