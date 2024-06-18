package com.example.b09_wqga.repository

import android.util.Log
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.database.Word
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class VocRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("vocs")

    suspend fun addVoc(voc: Voc): Boolean {
        return try {
            val newId = database.push().key ?: return false
            val newVoc = voc.copy(voc_id = voc.voc_id)
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

    suspend fun getWordsByVocId(vocId: Int): List<Word> {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(vocId.toDouble()).get().await()
            val voc = snapshot.children.firstOrNull()?.getValue(Voc::class.java)
            voc?.words_json ?: emptyList()
        } catch (e: Exception) {
            Log.e("VocRepository", "Error getting words by voc_id", e)
            emptyList()
        }
    }

    suspend fun addWord(vocId: Int, word: Word): Boolean {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(vocId.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            val voc = snapshot.children.firstOrNull()?.getValue(Voc::class.java) ?: return false

            val updatedWords = voc.words_json.toMutableList()

            // 현재 가장 높은 word_id를 찾아 새로운 word_id 설정
            val newWordId = (voc.words_json.maxOfOrNull { it.word_id } ?: 0) + 1
            val newWord = word.copy(word_id = newWordId)

            updatedWords.add(newWord)
            val updatedVoc = voc.copy(words_json = updatedWords, word_count = voc.word_count + 1)

            database.child(key).setValue(updatedVoc).await()
            return true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error adding word", e)
            false
        }
    }

    suspend fun updateWord(vocId: Int, word: Word): Boolean {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(vocId.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            val voc = snapshot.children.firstOrNull()?.getValue(Voc::class.java) ?: return false
            val updatedWords = voc.words_json.toMutableList().apply {
                val index = indexOfFirst { it.word_id == word.word_id }
                if (index != -1) {
                    set(index, word)
                }
            }
            database.child(key).child("words_json").setValue(updatedWords).await()
            true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error updating word", e)
            false
        }
    }

    suspend fun deleteWord(vocId: Int, wordId: Int): Boolean {
        return try {
            val snapshot = database.orderByChild("voc_id").equalTo(vocId.toDouble()).get().await()
            val key = snapshot.children.firstOrNull()?.key ?: return false
            val voc = snapshot.children.firstOrNull()?.getValue(Voc::class.java) ?: return false
            val updatedWords = voc.words_json.filterNot { it.word_id == wordId }
            val updatedVoc = voc.copy(words_json = updatedWords, word_count = voc.word_count - 1)

            database.child(key).setValue(updatedVoc).await()
            return true
        } catch (e: Exception) {
            Log.e("VocRepository", "Error deleting word", e)
            false
        }
    }
}

