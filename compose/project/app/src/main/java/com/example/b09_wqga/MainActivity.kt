package com.example.b09_wqga

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.b09_wqga.database.User
import com.example.b09_wqga.ui.theme.B09_wqgaTheme
import com.example.b09_wqga.util.SharedPreferencesHelper
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            B09_wqgaTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = remember { SharedPreferencesHelper(context) }
    var user by remember { mutableStateOf(prefs.getUser()) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "${user?.name ?: "사용자"}님 환영합니다")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            prefs.clearUser()
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as ComponentActivity).finish()
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text("로그아웃")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showPasswordDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text("비밀번호 변경")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showNameDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            )
        ) {
            Text("이름 변경")
        }
    }

    if (showPasswordDialog) {
        PasswordChangeDialog(
            user = user,
            onDismiss = { showPasswordDialog = false },
            onSave = { newPassword ->
                updateUserPassword(user?.user_id ?: 0, newPassword) {
                    showPasswordDialog = false
                    Toast.makeText(context, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showNameDialog) {
        NameChangeDialog(
            user = user,
            onDismiss = { showNameDialog = false },
            onSave = { newName ->
                user?.let {
                    val updatedUser = it.copy(name = newName)
                    updateUserName(updatedUser.user_id, newName) {
                        user = updatedUser
                        prefs.saveUser(updatedUser)
                        showNameDialog = false
                        Toast.makeText(context, "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}

@Composable
fun PasswordChangeDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("비밀번호 변경") },
        text = {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("새 비밀번호") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(newPassword) }) {
                Text("저장")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun NameChangeDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("이름 변경") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("새 이름") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(newName) }) {
                Text("저장")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

fun updateUserPassword(userId: Int, newPassword: String, onComplete: () -> Unit) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    database.child(userId.toString()).child("password").setValue(newPassword)
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener {}
}

fun updateUserName(userId: Int, newName: String, onComplete: () -> Unit) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    database.child(userId.toString()).child("name").setValue(newName)
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener {}
}
