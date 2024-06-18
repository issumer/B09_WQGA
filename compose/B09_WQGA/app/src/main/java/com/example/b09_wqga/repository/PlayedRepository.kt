package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Played
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PlayedRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("played")

    suspend fun addPlayed(played: Played): Boolean {
        return try {
            val newId = database.push().key ?: return false
            database.child(newId).setValue(played).await()
            true
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error adding played", e)
            false
        }
    }

    suspend fun getPlayedByUserId(userId: Int): List<Played> {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(userId.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(Played::class.java) }
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error getting all played by user_id", e)
            emptyList()
        }
    }

    // 기타 필요한 메소드 추가
}
