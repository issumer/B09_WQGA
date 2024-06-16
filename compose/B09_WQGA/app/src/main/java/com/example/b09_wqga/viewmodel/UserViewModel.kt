package com.example.b09_wqga.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.User
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.util.SharedPreferencesHelper
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val name = mutableStateOf("")
    val username = mutableStateOf("")
    val enterdate = mutableStateOf("")
    val points = mutableStateOf(0)

    fun fetchName(userId: String) {
        viewModelScope.launch {
            val fetchedName = userRepository.getName(userId)
            name.value = fetchedName ?: ""
        }
    }
    fun fetchUsername(userId: String) {
        viewModelScope.launch {
            val fetchedUsername = userRepository.getUsername(userId)
            username.value = fetchedUsername ?: ""
        }
    }

    fun fetchEnterDate(userId: String) {
        viewModelScope.launch {
            val fetchedEnterDate = userRepository.getEnterDate(userId)
            enterdate.value = fetchedEnterDate ?: ""
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
            if (userRepository.isUsernameTaken(user.username)) {
                onComplete(false)
            } else {
                val result = userRepository.addUser(user)
                onComplete(result == null)
            }
        }
    }

    fun loginUser(context: Context, username: String, password: String, onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(username, password)
            if (user != null) {
                SharedPreferencesHelper(context).saveUser(user)
            }
            onComplete(user)
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            SharedPreferencesHelper(context).clearUser()
            onComplete()
        }
    }

    fun updateUserDate(userId: String, date: String) {
        viewModelScope.launch {
            userRepository.updateUserDate(userId, date)
        }
    }
}
