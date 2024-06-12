package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Voc
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class VocRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("vocs")

    suspend fun addVoc(voc: Voc): Boolean {
        return try {
            val newId = database.push().key ?: return false
            val newVoc = voc.copy(voc_id = newId.hashCode())
            database.child(newId).setValue(newVoc).await()
            true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error adding voc", e)
            false
        }
    }

    suspend fun getVoc(voc_id: Int): Voc? {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(voc_id.toDouble()).get().await()
            snapshot.children.firstOrNull()?.getValue(Voc::class.java)
        } catch (e: Exception) {
            Log.e("VocRepository", "Error getting voc", e)
            null
        }
    }

    suspend fun updateVoc(voc: Voc): Boolean {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(voc.voc_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            database.child(key).setValue(voc).await()
            true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error updating voc", e)
            false
        }
    }

    suspend fun deleteVoc(voc_id: Int): Boolean {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(voc_id.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            database.child(key).removeValue().await()
            true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error deleting voc", e)
            false
        }
    }

    suspend fun getAllVocsByUserId(user_id: Int): List<Voc> {
        return try {
            val snapshot = database.orderByChild("user_id").equalTo(user_id.toDouble()).get().await()
            snapshot.children.mapNotNull { it.getValue(Voc::class.java) }
        } catch (e: Exception) {
            Log.e("VocRepository", "Error getting vocs by user_id", e)
            emptyList()
        }
    }
}
