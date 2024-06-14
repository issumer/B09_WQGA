package com.example.b09_wqga.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.database.Word
import com.example.b09_wqga.repository.VocRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VocViewModel(private val vocRepository: VocRepository) : ViewModel() {

    private val _vocList = MutableStateFlow<List<Voc>>(emptyList())
    val vocList: StateFlow<List<Voc>> = _vocList

    private val _wordList = MutableStateFlow<List<Word>>(emptyList())
    val wordList: StateFlow<List<Word>> = _wordList

    val searchText = MutableStateFlow("")
    val sortBasedOn = MutableStateFlow("Headword")
    val sortOrder = MutableStateFlow("Ascending")
    val sortOptions = listOf("Headword", "Meaning", "Right", "Wrong")

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun loadVocs(userId: Int) {
        viewModelScope.launch {
            val vocs = vocRepository.getAllVocsByUserId(userId)
            _vocList.value = vocs
        }
    }

    fun loadWordsByVocId(vocId: Int) {
        viewModelScope.launch {
            val words = vocRepository.getWordsByVocId(vocId)
            _wordList.value = words
        }
    }

    fun getWordById(wordId: Int): Word? {
        return _wordList.value.find { it.word_id == wordId }
    }

    fun addVoc(voc: Voc, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val vocWithId = voc.copy(voc_id = generateUniqueVocId(), create_date = getCurrentDateTime())
            val result = vocRepository.addVoc(vocWithId)
            onComplete(result)
            if (result) {
                loadVocs(voc.user_id)
            }
        }
    }

    fun getVocById(vocId: Int): Voc? {
        return _vocList.value.find { it.voc_id == vocId }
    }

    fun updateVoc(voc: Voc, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vocRepository.updateVoc(voc.copy(create_date = getCurrentDateTime()))
            onComplete(result)
            if (result) {
                loadVocs(voc.user_id)
            }
        }
    }

    fun deleteVoc(vocId: Int, userId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vocRepository.deleteVoc(vocId)
            onComplete(result)
            if (result) {
                loadVocs(userId)
            }
        }
    }

    fun addWord(vocId: Int, headword: String, meanings: List<String>) {
        viewModelScope.launch {
            val newWord = Word(
                word_id = generateUniqueWordId(vocId),
                voc_id = vocId,
                headword = headword,
                meanings = meanings,
                create_date = getCurrentDateTime()
            )
            val success = vocRepository.addWord(vocId, newWord)
            if (success) {
                updateWordCount(vocId, 1) // word_count 증가
            }
            loadWordsByVocId(vocId)
        }
    }

    fun deleteWord(vocId: Int, wordId: Int) {
        viewModelScope.launch {
            val success = vocRepository.deleteWord(vocId, wordId)
            if (success) {
                updateWordCount(vocId, -1) // word_count 감소
            }
            loadWordsByVocId(vocId)
        }
    }

    private suspend fun updateWordCount(vocId: Int, delta: Int) {
        val voc = getVocById(vocId)
        if (voc != null) {
            val updatedVoc = voc.copy(word_count = voc.word_count + delta, create_date = getCurrentDateTime())
            vocRepository.updateVoc(updatedVoc)
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            val success = vocRepository.updateWord(word.voc_id, word.copy(create_date = getCurrentDateTime()))
            if (success) {
                loadWordsByVocId(word.voc_id)
            }
        }
    }

    private fun generateUniqueVocId(): Int {
        return (_vocList.value.maxOfOrNull { it.voc_id } ?: 0) + 1
    }

    private fun generateUniqueWordId(vocId: Int): Int {
        val wordsInVoc = _wordList.value.filter { it.voc_id == vocId }
        return (wordsInVoc.maxOfOrNull { it.word_id } ?: 0) + 1
    }

    fun checkWordFull(): Boolean {
        return _wordList.value.size >= 10
    }

    fun isVocListFull(): Boolean {
        return _vocList.value.size >= 10
    }

    fun sortWordList() {
        val sortedList = when (sortBasedOn.value) {
            "Headword" -> if (sortOrder.value == "Ascending") {
                _wordList.value.sortedBy { it.headword }
            } else {
                _wordList.value.sortedByDescending { it.headword }
            }
            "Meaning" -> if (sortOrder.value == "Ascending") {
                _wordList.value.sortedBy { it.meanings.joinToString() }
            } else {
                _wordList.value.sortedByDescending { it.meanings.joinToString() }
            }
            "Right" -> if (sortOrder.value == "Ascending") {
                _wordList.value.sortedBy { it.right }
            } else {
                _wordList.value.sortedByDescending { it.right }
            }
            "Wrong" -> if (sortOrder.value == "Ascending") {
                _wordList.value.sortedBy { it.wrong }
            } else {
                _wordList.value.sortedByDescending { it.wrong }
            }
            else -> _wordList.value
        }
        _wordList.value = sortedList
    }
}
