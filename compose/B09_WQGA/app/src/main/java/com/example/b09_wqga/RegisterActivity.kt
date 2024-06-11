package com.example.b09_wqga

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.b09_wqga.database.User
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.ui.theme.B09_wqgaTheme
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date

class RegisterActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            B09_wqgaTheme {
                RegisterScreen(userViewModel, navigateToLogin = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun RegisterScreen(viewModel: UserViewModel, navigateToLogin: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dateFormat = "yyyyMMdd HH:mm"
    val date = Date(System.currentTimeMillis())
    val DateFormat = SimpleDateFormat(dateFormat)
    val Date: String = DateFormat.format(date)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("아이디") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val user = User(username = username, password = password, name = name, enterDate = Date,
                updateDate = Date)
            viewModel.registerUser(user) { success ->
                if (success) {
                    showDialog = true
                } else {
                    errorMessage = "회원가입에 실패했습니다."
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )) {
            Text("회원가입")
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("회원가입 성공") },
                text = { Text("회원이 되신 것을 축하합니다!") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        navigateToLogin()
                    }) {
                        Text("확인")
                    }
                }
            )
        }
    }
}
