package com.example.b09_wqga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.database.Attendance
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.ui.theme.B09_wqgaTheme
import com.example.b09_wqga.util.SharedPreferencesHelper
import com.example.b09_wqga.viewmodel.AttendanceViewModel
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodelfactory.AttendanceViewModelFactory
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserRepository())
    }
    private val attendanceViewModel: AttendanceViewModel by viewModels {
        AttendanceViewModelFactory(AttendanceRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = SharedPreferencesHelper(this)
        val user = prefs.getUser()

        if (user != null) {
            userViewModel.loginUser(user.username, user.password) { user ->
                if (user != null) {
                    updateAttendance(user.user_id)
                    navigateToMainActivity()
                }
            }
        }

        setContent {
            B09_wqgaTheme {
                LoginScreen(userViewModel, attendanceViewModel, navigateToRegister = {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }, navigateToMain = {
                    navigateToMainActivity()
                })
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateAttendance(user_id: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())

        val attendance = Attendance(user_id = user_id, attendance_date = date)
        attendanceViewModel.addOrUpdateAttendance(attendance) { success ->
            if (!success) {
                Toast.makeText(this, "출석 정보 업데이트 실패", Toast.LENGTH_LONG).show()
            } else {
                Log.d("LoginActivity", "Attendance updated successfully for user_id: $user_id")
            }
        }
    }
}

@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    attendanceViewModel: AttendanceViewModel,
    navigateToRegister: () -> Unit,
    navigateToMain: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember { SharedPreferencesHelper(context) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome To WQQA",
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("아이디") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    userViewModel.loginUser(username, password) { user ->
                        if (user != null) {
                            prefs.saveUser(user)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val date = dateFormat.format(Date())
                            val attendance = Attendance(user_id = user.user_id, attendance_date = date)
                            attendanceViewModel.addOrUpdateAttendance(attendance) { success ->
                                if (success) {
                                    Log.d("LoginScreen", "Attendance updated successfully for user_id: ${user.user_id}")
                                    navigateToMain()
                                } else {
                                    Log.e("LoginScreen", "Failed to update attendance for user_id: ${user.user_id}")
                                    Toast.makeText(context, "출석 정보 저장 실패", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Log.e("LoginScreen", "Login failed for username: $username")
                            Toast.makeText(context, "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )) {
                    Text("로그인")
                }
                Button(onClick = navigateToRegister,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )) {
                    Text("회원가입")
                }
            }
        }
    }
}
