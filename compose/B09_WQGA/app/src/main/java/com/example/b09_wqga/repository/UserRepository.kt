package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val maxIdRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("maxId")

    suspend fun addUser(user: User): Boolean {
        return try {
            val maxIdSnapshot = maxIdRef.get().await()
            val maxId = maxIdSnapshot.getValue(Int::class.java) ?: 0
            val newId = maxId + 1
            val newUser = user.copy(user_id = newId)
            database.child(newId.toString()).setValue(newUser).await()
            maxIdRef.setValue(newId).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            false
        }
    }

    suspend fun getUser(user_id: Int): User? {
        return try {
            val snapshot = database.child(user_id.toString()).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user", e)
            null
        }
    }

    suspend fun getUserByUsernameAndPassword(username: String, password: String): User? {
        return try {
            val snapshot = database.orderByChild("username").equalTo(username).get().await()
            val user = snapshot.children.mapNotNull { it.getValue(User::class.java) }.firstOrNull {
                it.password == password
            }
            if (user == null) {
                Log.d("UserRepository", "User not found or password incorrect for username: $username")
            } else {
                Log.d("UserRepository", "User found: $user")
            }
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user by username and password", e)
            null
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            database.child(user.user_id.toString()).setValue(user).await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
            false
        }
    }

    suspend fun deleteUser(user_id: Int): Boolean {
        return try {
            database.child(user_id.toString()).removeValue().await()
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error deleting user", e)
            false
        }
    }
}
