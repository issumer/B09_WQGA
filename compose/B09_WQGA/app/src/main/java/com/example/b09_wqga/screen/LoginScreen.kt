@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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


@OptIn(ExperimentalMaterial3Api::class)
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
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }




    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = painterResource(id = R.drawable.accounticon), contentDescription = null,
            modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(width = 280.dp, height = 50.dp)
                .background(Color.LightGray)

        ) {
            OutlinedTextField(
                value = userID,
                placeholder = {
                    Text(text = "Enter ID", color = Color.Gray, fontSize = 14.sp)
                },
                onValueChange = { userID = it },
                leadingIcon = { Icon(painter = painterResource(R.drawable.idlogo), contentDescription = null,
                    modifier = Modifier.size(25.dp)) },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(width = 280.dp, height = 50.dp)
                .background(Color.LightGray)

        ) {
            OutlinedTextField(
                value = userPassword,
                onValueChange = { userPassword = it },
                placeholder = {
                    Text(text = "Enter PW", color = Color.Gray, fontSize = 14.sp)
                },
                leadingIcon = { Icon(painter = painterResource(R.drawable.pwlogo), contentDescription = null,
                    modifier = Modifier.size(25.dp)) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.eye),
                            contentDescription = "Visibility Icon",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Image(painter = painterResource(id = R.drawable.loginbutton),
            contentDescription = null,
            modifier = Modifier.clickable {
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
            }
                .size(width = 100.dp, height = 41.dp)
        )


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
