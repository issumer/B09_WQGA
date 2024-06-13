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
import androidx.compose.ui.platform.LocalContext
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
import com.example.b09_wqga.database.User
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val userRepository = UserRepository()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var userID by rememberSaveable { mutableStateOf("") }
    var userPassword by rememberSaveable { mutableStateOf("") }
    var userName by rememberSaveable { mutableStateOf("") }
    var showRegisterFailDialog by rememberSaveable { mutableStateOf(false) }
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
        Image(painter = painterResource(id = R.drawable.pencil), contentDescription = null,
            modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(width = 280.dp, height = 50.dp)
                .background(Color.LightGray)

        ) {
            OutlinedTextField(
                value = userName,
                placeholder = {
                    Text(text = "Enter Name", color = Color.Gray, fontSize = 14.sp)
                },
                onValueChange = { userName = it },
                leadingIcon = { Icon(painter = painterResource(R.drawable.name), contentDescription = null,
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
        Spacer(modifier = Modifier.height(8.dp))
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
        Spacer(modifier = Modifier.height(8.dp))

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

        Image(painter = painterResource(id = R.drawable.joinbutton),
            contentDescription = null,
            modifier = Modifier.clickable {
                // 회원가입 체크 코드
                if(userID.isEmpty() || userPassword.isEmpty() || userName.isEmpty()) {
                    showRegisterFailDialog = true
                } else {
                    val user = User(username = userID, password = userPassword, name = userName, enterDate = Date, updateDate = Date)
                    userViewModel.registerUser(user) { success ->
                        if (success) {
                            uiViewModel.showBottomNavigationBar.value = true
                            navController.navigate(Routes.MainScreen.route) {
                                popUpTo(Routes.RegisterScreen.route) {
                                    inclusive = true
                                }
                                popUpTo(Routes.InitialScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else {
                            showRegisterFailDialog = true
                        }
                    }
                }
            }
                .size(width = 100.dp, height = 41.dp)
        )

        if (showRegisterFailDialog) {
            RegisterFailDialog { showRegisterFailDialog = false }
        }
    }
}

@Composable
fun RegisterFailDialog(onConfirmClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(text = "회원가입 실패", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = { Text(text = "회원가입에 실패하였습니다. 아이디, 비번, 이름이 비었는지 확인해주세요!") },
        confirmButton = {
            Button(onClick = onConfirmClick) {
                Text(text = "OK")
            }
        }
    )
}
