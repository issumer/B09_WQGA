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

    suspend fun getPlayed(user_id: Int, game_id: Int): Played? {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(user_id.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(Played::class.java) }
                .firstOrNull { it.game_id == game_id }
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error getting played", e)
            null
        }
    }

    suspend fun updatePlayed(played: Played): Boolean {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(played.user_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull { it.getValue(Played::class.java)?.game_id == played.game_id }?.key ?: return false
            database.child(key).setValue(played).await()
            true
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error updating played", e)
            false
        }
    }

    suspend fun deletePlayed(user_id: Int, game_id: Int): Boolean {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(user_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull { it.getValue(Played::class.java)?.game_id == game_id }?.key ?: return false
            database.child(key).removeValue().await()
            true
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error deleting played", e)
            false
        }
    }

    suspend fun getAllPlayedByUserId(user_id: Int): List<Played> {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(user_id.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(Played::class.java) }
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error getting all played by user_id", e)
            emptyList()
        }
    }

    suspend fun getAllPlayedByGameId(game_id: Int): List<Played> {
        return try {
            val snapshot = database.orderByChild("game_id").equalTo(game_id.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(Played::class.java) }
        } catch (e: Exception) {
            Log.e("PlayedRepository", "Error getting all played by game_id", e)
            emptyList()
        }
    }
}
