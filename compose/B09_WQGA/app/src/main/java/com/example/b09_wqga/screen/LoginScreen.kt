package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.b09_wqga.viewmodel.MiscViewModel
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodel.AttendanceViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import com.example.b09_wqga.viewmodelfactory.AttendanceViewModelFactory
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.repository.VocRepository
import java.text.SimpleDateFormat
import java.util.Date
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val userRepository = UserRepository()
    val attendanceRepository = AttendanceRepository()
    val vocRepository = VocRepository()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceRepository))
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
    val miscViewModel: MiscViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var userID by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var showLoginFailDialog by rememberSaveable { mutableStateOf(false) }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val dateFormat = "yyyyMMdd HH:mm"
    val date = Date(System.currentTimeMillis())
    val DateFormat = SimpleDateFormat(dateFormat)
    val Date: String = DateFormat.format(date)

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
                    Text(text = "Enter ID", color = Color.Gray, fontSize = 14.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Normal)
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
                    Text(text = "Enter PW", color = Color.Gray, fontSize = 14.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Normal)
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
            modifier = Modifier
                .clickable {
                    if (userID.isEmpty() || userPassword.isEmpty()) {
                        showLoginFailDialog = true
                    } else {
                        userViewModel.loginUser(context, userID, userPassword) { user ->
                            if (user != null) {
                                miscViewModel.showBottomNavigationBar.value = true
                                miscViewModel.userID.value = user.user_id.toString() // 임시

                                // 처음 등록한 사용자만 새 기본 단어장을 부여받음
                                if(user.enterDate == user.updateDate) {
                                    vocViewModel.addDefaultVoc(user.user_id)
                                }
                                userViewModel.updateUserDate(user.user_id.toString(), Date) // 로그인한 유저의 날짜 업데이트

                                navigateToMainScreen(navController, user.user_id.toString())
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

private fun navigateToMainScreen(navController: NavHostController, userId: String) {
    navController.navigate("${Routes.MainScreen.route}/$userId") {
        popUpTo(navController.graph.id) {// 백스택 모두 지우기
            inclusive = true
        }
        launchSingleTop = true
    }
}


@Composable
fun LoginFailDialog(onConfirmClick: () -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(text = "로그인 실패", fontSize = 20.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "아이디와 비밀번호를 확인해주세요!",
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

                TextButton(onClick = onConfirmClick) {
                    Image(painter = painterResource(R.drawable.okbutton),
                        contentDescription = null,
                        modifier = Modifier.size(width = 80.dp, height = 30.dp)
                    )
                }
            }
        }
    )
}
