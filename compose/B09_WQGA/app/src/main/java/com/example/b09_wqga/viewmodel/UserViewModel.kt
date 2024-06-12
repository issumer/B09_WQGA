package com.example.b09_wqga.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.User
import com.example.b09_wqga.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("UserViewModel", "Registering user: ${user.username}")
            if (userRepository.isUsernameTaken(user.username)) {
                onComplete(false)
            } else {
                val result = userRepository.addUser(user)
                onComplete(result == null)
            }
        }
    }

    fun loginUser(username: String, password: String, onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(username, password)
            onComplete(user)
        }
    }
}
