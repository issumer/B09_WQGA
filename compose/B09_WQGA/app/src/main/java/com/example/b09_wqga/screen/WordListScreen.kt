package com.example.b09_wqga.screen

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.component.SearchBar
import com.example.b09_wqga.database.Word
import com.example.b09_wqga.model.GTranslation
import com.example.b09_wqga.model.translateText
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun WordListScreen(navController: NavHostController, vocId: Int) {
    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
    val context = LocalContext.current // 현재 컨텍스트 정보

    val lazyColumnWordList by vocViewModel.wordList.collectAsState()

    var currentEditWordId by remember { // 현재 편집하는 단어의 ID
        mutableStateOf(0)
    }

    var showWordAddDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var showWordEditDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var showWordAddFailDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var ttsReady by rememberSaveable { // tts 준비 여부
        mutableStateOf(false)
    }
    var tts: TextToSpeech? by remember { // tts가 준비되면 실제 객체로 변경
        mutableStateOf(null)
    }

    LaunchedEffect(currentEditWordId) {
        if(tts == null) {
            tts = TextToSpeech(context) { status -> // tts 객체 초기화
                if (status == TextToSpeech.SUCCESS) {
                    ttsReady = true
                    tts!!.language = Locale.US
                }
            }
        }
    }

    // 현재 Lifecycle owner가 dispose되면 수행
    DisposableEffect(LocalLifecycleOwner.current) {
        // 화면에서 감춰지거나 하면 자동으로 호출해서 tts 멈춤
        onDispose {
            tts?.stop() // nullable
            tts?.shutdown()
        }
    }

    val speakWord = { wordData: Word ->
        if (ttsReady) {
            // 큐에 추가 (순차적으로)
            tts?.speak(wordData.headword, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    // 현재 이 단어 목록 화면의 단어장
    LaunchedEffect(vocId) {
        vocViewModel.loadWordsByVocId(vocId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                searchText = vocViewModel.searchText.value,
                onSearchTextChanged = { vocViewModel.searchText.value = it }
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button_WQGA(width = 80, height = 40, text = "Add",
                onClickLabel = {
                    if (vocViewModel.checkWordFull()) {
                        showWordAddFailDialog = true
                    } else {
                        showWordAddDialog = true
                    }
                },
                enabled = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SortDropdownMenu(
                label = "Sort Based On",
                selectedOption = vocViewModel.sortBasedOn.value,
                options = vocViewModel.sortOptions,
                onOptionSelected = {
                    vocViewModel.sortBasedOn.value = it
                    vocViewModel.sortWordList()
                }
            )

            SortDropdownMenu(
                label = "Sort Order",
                selectedOption = vocViewModel.sortOrder.value,
                options = listOf("Ascending", "Descending"),
                onOptionSelected = {
                    vocViewModel.sortOrder.value = it
                    vocViewModel.sortWordList()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(lazyColumnWordList) { wordData ->
                WordItem(
                    word = wordData,
                    onTTSClick = {
                        speakWord(wordData)
                    },
                    onEditClick = {
                        currentEditWordId = wordData.word_id
                        showWordEditDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if (showWordAddDialog) {
            WordAddDialog(
                onDismiss = { showWordAddDialog = false },
                onAddWord = { headword, meanings ->
                    vocViewModel.addWord(vocId, headword, meanings)
                    showWordAddDialog = false
                }
            )
        }
        if (showWordEditDialog) {
            val wordData = vocViewModel.getWordById(currentEditWordId)
            if (wordData != null) {
                WordEditDialog(
                    onDismiss = { showWordEditDialog = false },
                    currentWord = wordData,
                    onDictClick = { headword ->
                        vocViewModel.loadWordsByVocId(vocId) // 사전 API 대신 샘플 로드
                    },
                    onSaveWord = { headword, meanings ->
                        vocViewModel.updateWord(wordData.copy(headword = headword, meanings = meanings))
                        showWordEditDialog = false
                    }
                ) {
                    vocViewModel.deleteWord(vocId, currentEditWordId)
                    showWordEditDialog = false
                }
            }
        }
        if (showWordAddFailDialog) {
            WordAddFailDialog {
                showWordAddFailDialog = false
            }
        }
    }
}

@Composable
fun WordItem(word: Word, onTTSClick: () -> Unit, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = word.headword,
            fontSize = 20.sp,
            fontFamily = pixelFont2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        word.meanings.forEach { meaning ->
            if (meaning.isNotEmpty()) {
                Text(text = meaning, fontSize = 16.sp, fontFamily = pixelFont2, modifier = Modifier.padding(bottom = 4.dp))
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
                Button_WQGA(width = 80, height = 40, text = "TTS",
                    onClickLabel = {
                        onTTSClick()
                    },
                    enabled = true
                )

                Spacer(modifier = Modifier.width(8.dp))


                Button_WQGA(width = 80, height = 40, text = "Edit",
                    onClickLabel = {
                        onEditClick()
                    },
                    enabled = true
                )
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
                }, text = { Text(text = option) })
            }
        }
    }
}

@Composable
fun WordAddDialog(onDismiss: () -> Unit, onAddWord: (String, List<String>) -> Unit) {
    val scope = rememberCoroutineScope()
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
        title = { Text(text = "Add Word",fontFamily = pixelFont1, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button_WQGA(width = 80, height = 40, text = "Back",
                    onClickLabel = onDismiss,
                    enabled = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = headword,
                        onValueChange = { headword = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Enter Headword") }
                    )
                    Button_WQGA(width = 40, height = 20, text = "Dict",
                        onClickLabel = {
                            if(!headword.isEmpty()) {
                                scope.launch {
                                    val translations : List<GTranslation> = translateText(headword)
                                    for(i in 0..4) {
                                        meanings[i] = ""
                                        if (i < translations.size) {
                                            meanings[i] = translations[i].translatedText
                                        }
                                    }
                                }
                            }
                        },
                        enabled = true
                    )
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
            Button_WQGA(width = 80, height = 40, text = "Add",
                onClickLabel = {
                    var canAddWord = true
                    if (headword.isEmpty()) {
                        warningMessage = "Headword를 채워주세요!"
                        canAddWord = false
                    }

                    var everyMeaningEmpty = true
                    meanings.forEach { meaning ->
                        if (meaning.isNotEmpty()) everyMeaningEmpty = false
                    }

                    if (everyMeaningEmpty && canAddWord) {
                        warningMessage = "Meaning을 채워주세요!"
                        canAddWord = false
                    }

                    if (canAddWord) {
                        onAddWord(headword, meanings.toList())
                    }

                },
                enabled = true
            )
        }
    )
}

@Composable
fun WordEditDialog(onDismiss: () -> Unit, currentWord: Word, onDictClick: (String) -> Unit, onSaveWord: (String, List<String>) -> Unit, onDeleteWord: () -> Unit) {
    val scope = rememberCoroutineScope()
    var headword by remember {
        mutableStateOf(currentWord.headword)
    }
    var meanings = remember {
        mutableStateListOf("", "", "", "", "")
    }
    var warningMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(currentWord) {
        val currentMeanings = currentWord.meanings.toTypedArray()
        for(i in 0..4) {
            if(i < currentMeanings.size) {
                meanings[i] = currentMeanings[i]
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Word",fontFamily = pixelFont1, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button_WQGA(width = 80, height = 40, text = "Back",
                    onClickLabel = onDismiss,
                    enabled = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = headword,
                        onValueChange = { headword = it },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        label = { Text("Headword") }
                    )
                    Button_WQGA(width = 40, height = 20, text = "Dict",
                        onClickLabel = {
                            if(!headword.isEmpty()) {
                                scope.launch {
                                    val translations : List<GTranslation> = translateText(headword)
                                    for(i in 0..4) {
                                        meanings[i] = ""
                                        if (i < translations.size) {
                                            meanings[i] = translations[i].translatedText
                                        }
                                    }
                                }
                            }
                        },
                        enabled = true
                    )
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button_WQGA(width = 80, height = 40, text = "Delete",
                    onClickLabel = onDeleteWord,
                    enabled = true
                )
                Button_WQGA(width = 80, height = 40, text = "Save",
                    onClickLabel = {
                        var canSaveWord = true
                        var everyMeaningEmpty = true
                        meanings.forEach { meaning ->
                            if (meaning.isNotEmpty()) everyMeaningEmpty = false
                        }

                        if (everyMeaningEmpty) {
                            warningMessage = "Meaning을 채워주세요!"
                            canSaveWord = false
                        }

                        if (canSaveWord) {
                            onSaveWord(headword, meanings.toList())
                        }
                    },
                    enabled = true
                )
            }
        }
    )
}

@Composable
fun WordAddFailDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "단어 개수 제한", fontSize = 20.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "이 앱은 평가용 앱이므로 단어 개수에 제한이 있습니다. 양해 부탁드립니다!", fontFamily = pixelFont2)
            }
        },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "OK",
                onClickLabel = onDismiss,
                enabled = true
            )
        }
    )
}
