package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val maxIdRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("maxId")

    suspend fun addUser(user: User): String? {
        return try {
            Log.d("UserRepository", "Starting to add user: ${user.username}")
            val maxIdSnapshot = maxIdRef.get().await()
            val maxId = maxIdSnapshot.getValue(Int::class.java) ?: 0
            val newId = maxId + 1
            val newUser = user.copy(user_id = newId)
            database.child(newId.toString()).setValue(newUser).await()
            maxIdRef.setValue(newId).await()
            null // 성공 시 null 반환
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            e.message // 실패 시 예외 메시지 반환
        }
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val snapshot = database.orderByChild("username").equalTo(username).get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking username", e)
            false
        }
    }

    suspend fun loginUser(username: String, password: String): User? {
        return try {
            val snapshot = database.orderByChild("username").equalTo(username).get().await()
            val user = snapshot.children.mapNotNull { it.getValue(User::class.java) }.firstOrNull {
                it.password == password
            }
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Error logging in", e)
            null
        }
    }

    suspend fun getName(userId: String): String? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)?.name
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching name", e)
            null
        }
    }
    suspend fun getUsername(userId: String): String? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)?.username
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching username", e)
            null
        }
    }

    suspend fun getPoints(userId: String): Int? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)?.points
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching points", e)
            null
        }
    }

    suspend fun getEnterDate(userId: String): String? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)?.enterDate
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching enterDate", e)
            null
        }
    }

    suspend fun increasePoints(userId: String): Boolean {
        return try {
            val snapshot = database.child(userId).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                val newPoints = (user.points ?: 0) + 10
                database.child(userId).child("points").setValue(newPoints).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error increasing points", e)
            false
        }
    }
}
