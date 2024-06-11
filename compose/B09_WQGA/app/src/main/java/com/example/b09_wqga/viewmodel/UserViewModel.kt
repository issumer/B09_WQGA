package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.User
import com.example.b09_wqga.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.addUser(user)
            onComplete(result)
        }
    }

    fun loginUser(username: String, password: String, onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUserByUsernameAndPassword(username, password)
            onComplete(user)
        }
    }

    fun getUser(user_id: Int, onComplete: (User?) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getUser(user_id)
            onComplete(user)
        }
    }

    fun updateUser(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.updateUser(user)
            onComplete(result)
        }
    }

    fun deleteUser(user_id: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.deleteUser(user_id)
            onComplete(result)
        }
    }
}
