package com.example.b09_wqga

import UserRepository
import UserViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.b09_wqga.database.User
import java.text.SimpleDateFormat
import java.util.Date

class SignUpActivity : ComponentActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userRepository = UserRepository()
        val factory = UserViewModel.Factory(userRepository)
        viewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        setContent {
            RegisterScreen(viewModel)
        }
    }
}

@Composable
fun RegisterScreen(viewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (username.isEmpty()) Text(text = "Username")
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (password.isEmpty()) Text(text = "Password")
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (name.isEmpty()) Text(text = "Name")
                innerTextField()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        val dateFormat = "yyyyMMdd HH:mm"
        val date = Date(System.currentTimeMillis())
        val DateFormat = SimpleDateFormat(dateFormat)
        val Date: String = DateFormat.format(date)
        Button(onClick = {
            val user = User(
                username = username,
                password = password,
                name = name,
                enterDate = Date,
                updateDate = Date
            )
            viewModel.addUser(user)
            context.startActivity(Intent(context, LoginActivity::class.java))
        }) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }) {
            Text(text = "Back to Login")
        }
    }
}
