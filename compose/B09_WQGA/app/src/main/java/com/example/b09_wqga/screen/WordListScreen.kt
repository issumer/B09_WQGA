/*
구현 목록에서 단어 목록 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.R
import com.example.b09_wqga.component.SearchBar
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.VocData
import com.example.b09_wqga.model.WordData
import java.util.Locale


@Composable
fun WordListScreen() {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val context = LocalContext.current // 현재 컨텍스트 정보

    val lazyColumnWordList = userDataViewModel.lazyColumnWordList

    var currentEditHeadword by rememberSaveable { // 현재 편집하는 단어의 표제어
        mutableStateOf("")
    }

    var showWordAddDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var showWordEditDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var ttsReady by rememberSaveable { // tts 준비 여부
        mutableStateOf(false)
    }
    var tts : TextToSpeech? by remember { // tts가 준비되면 실제 객체로 변경
        mutableStateOf(null)
    }

    // 현재 Lifecycle owner가 dispose되면 수행
    DisposableEffect(LocalLifecycleOwner.current) {
        tts = TextToSpeech(context) {status -> // tts 객체 초기화
            if(status == TextToSpeech.SUCCESS) {
                ttsReady = true
                tts!!.language = Locale.US
            }
        }
        // 화면에서 감춰지거나 하면 자동으로 호출해서 tts 멈춤
        onDispose {
            tts?.stop() // nullable
            tts?.shutdown()
        }
    }

    val speakWord = { wordData:WordData ->
        if(ttsReady) {
            // 큐에 추가 (순차적으로)
            tts?.speak(wordData.headword, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    // 현재 이 단어 목록 화면의 단어장
    var currentlyEnteredVoc = userDataViewModel.findVocByUUID(userDataViewModel.currentlyEnteredVocUUID.value)

    if(currentlyEnteredVoc != null) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    searchText = userDataViewModel.wordListSearchText.value,
                    onSearchTextChanged = {
                        userDataViewModel.wordListSearchText.value =
                            it; userDataViewModel.updateLazyColumnWordList()
                    },
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    showWordAddDialog = true
                }) {
                    Text("Add") // Add Word
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortDropdownMenu(
                    label = "Sort Based On",
                    selectedOption = userDataViewModel.wordListSortBasedOn.value,
                    options = userDataViewModel.wordListSortBasedOnList,
                    onOptionSelected = {
                        userDataViewModel.wordListSortBasedOn.value =
                            it; userDataViewModel.updateLazyColumnWordList()
                    }
                )

                SortDropdownMenu(
                    label = "Sort Order",
                    selectedOption = userDataViewModel.wordListSortOrder.value,
                    options = userDataViewModel.wordListSortOrderList,
                    onOptionSelected = {
                        userDataViewModel.wordListSortOrder.value =
                            it; userDataViewModel.updateLazyColumnWordList()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(lazyColumnWordList) { wordData ->
                    WordItem(word = wordData,
                        onTTSClick = {
                            speakWord(wordData)
                        },
                        onEditClick = {
                            currentEditHeadword = wordData.headword
                            showWordEditDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            if (showWordAddDialog) {
                WordAddDialog(onDismiss = { showWordAddDialog = false },
                    currentVoc = currentlyEnteredVoc,
                    onDictClick = { headword ->
                        userDataViewModel.getBackendDict(headword)
                    },
                    onAddWord = { headword, meanings ->
                        userDataViewModel.addWord(headword, meanings)
                        showWordAddDialog = false
                    }
                )
            }
            if (showWordEditDialog) {
                if (!currentEditHeadword.isEmpty()) {
                    val wordData = userDataViewModel.findWordByHeadword(currentEditHeadword)

                    Log.i("worddata", wordData.toString())
                    if (wordData != null) {
                        Log.i("worddata2", wordData.toString())
                        WordEditDialog(onDismiss = { showWordEditDialog = false },
                            currentWord = wordData,
                            onDictClick = { headword ->
                                userDataViewModel.getBackendDict(headword)
                            },
                            onSaveWord = { headword, meanings ->
                                userDataViewModel.editWord(headword, meanings)
                                showWordEditDialog = false
                            },
                            onDeleteWord = {
                                userDataViewModel.deleteWord(currentEditHeadword)
                                showWordEditDialog = false
                            }
                        )
                    }
                }
            }
        }
    } else { // 단어장을 삭제하고 back 버튼을 눌렀을 경우 대비
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("현재 단어장이 존재하지 않습니다.")
            }
        }
    }
}

@Composable
fun WordItem(word: WordData, onTTSClick: () -> Unit, onEditClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = word.headword,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        word.meanings.forEach { meaning ->
            if(!meaning.isEmpty()) {
                Text(text = meaning, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${word.right}")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${word.wrong}")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    onTTSClick()
                }) {
                    Text("TTS")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    onEditClick()
                }) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
fun SortDropdownMenu(label: String, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.width(160.dp),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }, text = {Text(text = option)})
            }
        }
    }
}

@Composable
fun WordAddDialog(onDismiss: () -> Unit, currentVoc: VocData, onDictClick: (String) -> Array<String>, onAddWord: (String, Array<String>) -> Unit) {
    var headword by rememberSaveable {
        mutableStateOf("")
    }
    var meanings = remember {
        mutableStateListOf("", "", "", "", "")
    }
    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Word", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row (modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = headword,
                        onValueChange = { headword = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Enter Headword") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // 사전 API 실행 코드
                        var dictMeanings = onDictClick(headword)

                        // 뜻 초기화
                        meanings.clear()

                        // 처음 5개만 복사
                        if (dictMeanings.size >= 5) {
                            meanings.addAll(dictMeanings.copyOfRange(0, 5))
                        } else {
                            meanings.addAll(dictMeanings.copyOf())
                        }
                    }) {
                        Text("Dict")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                meanings.forEachIndexed { index, meaning ->
                    OutlinedTextField(
                        value = meaning,
                        onValueChange = { meanings[index] = it },
                        label = { Text("Meaning ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Button(onClick = {
                var canAddWord = true
                if(headword.isEmpty()) {
                    warningMessage = "Headword를 채워주세요!"
                    canAddWord = false
                }

                var everyMeaningEmpty = true
                meanings.forEach {meaning ->
                    if(!meaning.isEmpty()) everyMeaningEmpty = false
                }

                if(everyMeaningEmpty && canAddWord) {
                    warningMessage = "Meaning을 채워주세요!"
                    canAddWord = false
                }

                val sameHeadwordIndex = currentVoc.wordList.indexOfFirst {
                    it.headword == headword
                }
                if(sameHeadwordIndex != -1 && canAddWord) {
                    warningMessage = "중복된 표제어의 단어를 추가할 수 없습니다."
                    canAddWord = false
                }

                if(canAddWord) {
                    onAddWord(headword, meanings.toTypedArray())
                }

            }) {
                Text("Add Word")
            }
        }
    )
}

@Composable
fun WordEditDialog(onDismiss: () -> Unit, currentWord: WordData, onDictClick: (String) -> Array<String>, onSaveWord: (String, Array<String>) -> Unit, onDeleteWord: () -> Unit) {
    var headword by rememberSaveable {
        mutableStateOf(currentWord.headword)
    }
    var meanings = remember {
        mutableStateListOf(currentWord.meanings[0],
            currentWord.meanings[1],
            currentWord.meanings[2],
            currentWord.meanings[3],
            currentWord.meanings[4]
        )
    }
    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Word", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row (modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = headword,
                        onValueChange = { headword = it },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        label = { Text("Enter Headword") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // 사전 API 실행 코드
                        var dictMeanings = onDictClick(headword)

                        // 뜻 초기화
                        meanings.clear()

                        // 처음 5개만 복사
                        if (dictMeanings.size >= 5) {
                            meanings.addAll(dictMeanings.copyOfRange(0, 5))
                        } else {
                            meanings.addAll(dictMeanings.copyOf())
                        }
                    }) {
                        Text("Dict")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                meanings.forEachIndexed { index, meaning ->
                    OutlinedTextField(
                        value = meaning,
                        onValueChange = { meanings[index] = it },
                        label = { Text("Meaning ${index + 1}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDeleteWord) {
                    Text("Delete Word")
                }
                Button(onClick = {
                    var canAddWord = true
                    var everyMeaningEmpty = true
                    meanings.forEach {meaning ->
                        if(!meaning.isEmpty()) everyMeaningEmpty = false
                    }

                    if(everyMeaningEmpty) {
                        warningMessage = "Meaning을 채워주세요!"
                        canAddWord = false
                    }

                    if(canAddWord) {
                        onSaveWord(headword, meanings.toTypedArray())
                    }
                }) {
                    Text("Save Word")
                }
            }
        }
    )
}