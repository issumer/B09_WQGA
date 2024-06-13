package com.example.b09_wqga.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.User
import com.example.b09_wqga.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val username = mutableStateOf("")
    val points = mutableStateOf(0)

    fun fetchUsername(userId: String) {
        viewModelScope.launch {
            val fetchedUsername = userRepository.getUsername(userId)
            username.value = fetchedUsername ?: ""
        }
    }

    fun fetchPoints(userId: String) {
        viewModelScope.launch {
            val fetchedPoints = userRepository.getPoints(userId)
            points.value = fetchedPoints ?: 0
        }
    }

    fun increasePoints(userId: String) {
        viewModelScope.launch {
            val success = userRepository.increasePoints(userId)
            if (success) {
                points.value += 10
            }
        }
    }

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

