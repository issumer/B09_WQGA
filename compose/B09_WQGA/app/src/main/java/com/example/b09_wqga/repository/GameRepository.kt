package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Game
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("games")

    suspend fun addGame(game: Game): Boolean {
        return try {
            val newId = database.push().key ?: return false
            val newGame = game.copy(game_id = newId.hashCode())
            database.child(newId).setValue(newGame).await()
            true
        } catch (e: Exception) {
            Log.e("GameRepository", "Error adding game", e)
            false
        }
    }

    suspend fun getGame(game_id: Int): Game? {
        return try {
            val snapshot = database.orderByChild("game_id").equalTo(game_id.toDouble()).get().await()
            snapshot.children.firstOrNull()?.getValue(Game::class.java)
        } catch (e: Exception) {
            Log.e("GameRepository", "Error getting game", e)
            null
        }
    }

    suspend fun updateGame(game: Game): Boolean {
        return try {
            val snapshot = database.orderByChild("game_id").equalTo(game.game_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            database.child(key).setValue(game).await()
            true
        } catch (e: Exception) {
            Log.e("GameRepository", "Error updating game", e)
            false
        }
    }

    suspend fun deleteGame(game_id: Int): Boolean {
        return try {
            val snapshot = database.orderByChild("game_id").equalTo(game_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            database.child(key).removeValue().await()
            true
        } catch (e: Exception) {
            Log.e("GameRepository", "Error deleting game", e)
            false
        }
    }

    suspend fun getAllGames(): List<Game> {
        return try {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Game::class.java) }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error getting all games", e)
            emptyList()
        }
    }
}
