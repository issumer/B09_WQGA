package com.example.b09_wqga.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.component.SearchBar
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun VocListScreen(navController: NavHostController, userId: Int) {
    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
    val vocList by vocViewModel.vocList.collectAsState(initial = emptyList())

    var currentEditVocId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showVocAddDialog by rememberSaveable { mutableStateOf(false) }
    var showVocEditDialog by rememberSaveable { mutableStateOf(false) }
    var showVocAddFailDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userId) {
        vocViewModel.loadVocs(userId)
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
                    if (vocViewModel.isVocListFull()) {
                        showVocAddFailDialog = true
                    } else {
                        showVocAddDialog = true
                    }
                },
                enabled = true
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(vocList) { voc ->
                VocItem(
                    voc = voc,
                    onEditClick = {
                        currentEditVocId = voc.voc_id
                        showVocEditDialog = true
                    },
                    onEnterClick = {
                        navController.navigate("${Routes.WordListScreen.route.replace("{vocId}", voc.voc_id.toString())}") {
                            launchSingleTop = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showVocAddDialog) {
            VocAddDialog(
                onDismiss = { showVocAddDialog = false },
                onAddVoc = { title, description, language ->
                    vocViewModel.addVoc(Voc(title = title, description = description, lang = language, user_id = userId, words_json = emptyList())) { success ->
                        if (success) {
                            vocViewModel.loadVocs(userId)
                        }
                        showVocAddDialog = false
                    }
                }
            )
        }

        if (showVocEditDialog) {
            currentEditVocId?.let { vocId ->
                val voc = vocViewModel.getVocById(vocId)
                if (voc != null) {
                    VocEditDialog(
                        onDismiss = { showVocEditDialog = false },
                        currentEditVoc = voc,
                        onSaveVoc = { title, description ->
                            vocViewModel.updateVoc(voc.copy(title = title, description = description)) { success ->
                                if (success) {
                                    vocViewModel.loadVocs(userId)
                                }
                                showVocEditDialog = false
                            }
                        },
                        onDeleteVoc = {
                            vocViewModel.deleteVoc(vocId, userId) { success ->
                                if (success) {
                                    vocViewModel.loadVocs(userId)
                                }
                                showVocEditDialog = false
                            }
                        }
                    )
                }
            }
        }

        if (showVocAddFailDialog) {
            VocAddFailDialog {
                showVocAddFailDialog = false
            }
        }
    }
}


@Composable
fun VocItem(voc: Voc, onEditClick: () -> Unit, onEnterClick: () -> Unit) {
    Box(
        Modifier
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(all = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = voc.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = pixelFont2,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            Text(
                text = voc.description,
                fontSize = 16.sp,
                maxLines = 2,
                fontFamily = pixelFont2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Column(modifier = Modifier.fillMaxSize()
                .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.Bottom){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Abc,
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Icon"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Count: ${voc.word_count}", fontFamily = pixelFont2)

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Language,
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Icon"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = voc.lang, fontFamily = pixelFont2)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button_WQGA(width = 80, height = 40, text = "Edit",
                            onClickLabel = onEditClick,
                            enabled = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button_WQGA(width = 80, height = 40, text = "Enter",
                            onClickLabel = onEnterClick,
                            enabled = true
                        )
                    }
                }
            }

        }
    }
}
@Composable
fun VocAddDialog(onDismiss: () -> Unit, onAddVoc: (String, String, String) -> Unit) {
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var description by rememberSaveable {
        mutableStateOf("")
    }
    var selectedLanguageName by rememberSaveable {
        mutableStateOf("")
    }
    val languageList = listOf("en", "ko", "ja", "zh") // 임시 언어 리스트
    val filteredLanguages = languageList.filter {
        it.contains(selectedLanguageName, ignoreCase = true)
    }

    var languageExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Vocabulary", fontSize = 20.sp, fontFamily = pixelFont1, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Voc Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Voc Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedLanguageName,
                        onValueChange = {
                            selectedLanguageName = it
                        },
                        label = { Text("Select Language") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { languageExpanded = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        filteredLanguages.forEach { language ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedLanguageName = language
                                    languageExpanded = false
                                },
                                text = { Text(language) })
                        }
                    }
                }
                Text(text = warningMessage)
            }
        },

        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "Add",
                onClickLabel = {
                    if (title.isEmpty()) {
                        warningMessage = "Title을 채워주세요!"
                    } else if (description.isEmpty()) {
                        warningMessage = "Description을 채워주세요!"
                    } else if (selectedLanguageName.isEmpty()) {
                        warningMessage = "Language를 선택해주세요!"
                    } else {
                        warningMessage = ""
                        onAddVoc(title, description, selectedLanguageName)
                    }
                },
                enabled = true
            )
        }
    )
}

@Composable
fun VocEditDialog(onDismiss: () -> Unit, currentEditVoc: Voc, onSaveVoc: (String, String) -> Unit, onDeleteVoc: () -> Unit) {
    var title by rememberSaveable {
        mutableStateOf(currentEditVoc.title)
    }
    var description by rememberSaveable {
        mutableStateOf(currentEditVoc.description)
    }
    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Vocabulary",fontFamily = pixelFont1, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Voc Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Voc Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

                Button_WQGA(width = 80, height = 40, text = "Delete",
                    onClickLabel = onDeleteVoc,
                    enabled = true
                )
                Button_WQGA(width = 80, height = 40, text = "Save",
                    onClickLabel = {
                        if (title.isEmpty()) {
                            warningMessage = "Title을 채워주세요!"
                        } else if (description.isEmpty()) {
                            warningMessage = "Description을 채워주세요!"
                        } else {
                            warningMessage = ""
                            onSaveVoc(title, description)
                        }
                    },
                    enabled = true
                )
            }
        }
    )
}

@Composable
fun VocAddFailDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "단어장 개수 제한",fontFamily = pixelFont1, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "이 앱은 평가용 앱이므로 단어장 개수에 제한이 있습니다. 양해 부탁드립니다!")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("확인")
            }
        }
    )
}
