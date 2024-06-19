package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Played
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PlayedRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("played")

    suspend fun addOrUpdatePlayed(played: Played): Boolean {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(played.user_id.toDouble()).get().await()
            val existingPlayed = snapshot.children.firstOrNull { it.child("game_id").getValue(Int::class.java) == played.game_id }

            if (existingPlayed != null) {
                val existingPlayedData = existingPlayed.getValue(Played::class.java)
                if (existingPlayedData != null) {
                    val updatedPlayed = existingPlayedData.copy(
                        best_score = maxOf(existingPlayedData.best_score, played.best_score),
                        right = existingPlayedData.right + played.right,
                        wrong = existingPlayedData.wrong + played.wrong,
                        play_count = existingPlayedData.play_count + 1,
                        play_date = played.play_date
                    )
                    existingPlayed.ref.setValue(updatedPlayed).await()
                }
            } else {
                val newId = database.push().key ?: return false
                database.child(newId).setValue(played).await()
            }
            true
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error adding or updating played", e)
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
}

