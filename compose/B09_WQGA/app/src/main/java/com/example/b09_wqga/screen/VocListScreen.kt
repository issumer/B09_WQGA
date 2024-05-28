/*
구현 목록에서 단어장 목록 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.SearchBar
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.VocData
import com.example.b09_wqga.model.WordData
import com.example.b09_wqga.model.WordData.Companion.LANGUAGE_NAMES_IN_KOREAN
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.ui.theme.B09_WQGATheme


@Composable
fun VocListScreen(navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val lazyColumnVocList = userDataViewModel.lazyColumnVocList

    // 이 방식은 custom saver 필요
//    var currentEditVoc by rememberSaveable {
//        mutableStateOf<VocData?>(null)
//    }

    var currentEditVocUUID by rememberSaveable { // 현재 편집하는 단어장의 uuid
        mutableStateOf("")
    }

    var showVocAddDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }
    var showVocEditDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(searchText = userDataViewModel.vocListSearchText.value,
                onSearchTextChanged = { userDataViewModel.vocListSearchText.value = it; userDataViewModel.updateLazyColumnVocList() },
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                showVocAddDialog = true
            }) {
                Text("Add") // Add Voc
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(lazyColumnVocList) { vocData ->
                VocItem(vocData = vocData,
                    onEditClick = {
                        currentEditVocUUID = vocData.uuid
                        showVocEditDialog = true
                    },
                    onEnterClick = {
                        userDataViewModel.currentlyEnteredVocUUID.value = vocData.uuid
                        if(userDataViewModel.findVocByUUID(vocData.uuid) != null) {
                            userDataViewModel.updateLazyColumnWordList()
                            navController.navigate(Routes.WordListScreen.route) {
//                                popUpTo(Routes.VocListScreen.route) {
//                                    inclusive = true
//                                }
                                launchSingleTop = true
                            }
                        }
                    })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        if(showVocAddDialog) {
            VocAddDialog(onDismiss = {showVocAddDialog = false},
                onAddVoc = {title, description, languageName ->
                    userDataViewModel.addVoc(title, description, languageName)
                    showVocAddDialog = false
                }
            )
        }
        if(showVocEditDialog) {
            if(!currentEditVocUUID.isEmpty()) {
                val vocData = userDataViewModel.findVocByUUID(currentEditVocUUID)

                if(vocData != null) {
                    VocEditDialog(onDismiss = {showVocEditDialog = false},
                        currentEditVoc = vocData,
                        onSaveVoc = { title, description ->
                            userDataViewModel.editVoc(currentEditVocUUID, title, description)
                            showVocEditDialog = false
                        },
                        onDeleteVoc = {
                            userDataViewModel.deleteVoc(currentEditVocUUID)
                            showVocEditDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VocItem(vocData: VocData, onEditClick: () -> Unit, onEnterClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = vocData.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = vocData.description,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Abc,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Blue,
                    contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Count: ${vocData.wordCount}")

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Language,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Blue,
                    contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = WordData.LANGUAGE_NAMES_IN_KOREAN[vocData.lang]!!)
            }

            //Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onEditClick) {
                    Text("Edit") // Edit Voc
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onEnterClick) {
                    Text("Enter") // Enter Voc
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
    var languageList = WordData.LANGUAGE_NAMES_IN_KOREAN.values.toList()
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
        title = { Text(text = "Add Vocabulary", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            Button(onClick = {
                if(title.isEmpty()) {
                    warningMessage = "Title을 채워주세요!"
                }
                else if(description.isEmpty()) {
                    warningMessage = "Description을 채워주세요!"
                }
                else if(selectedLanguageName.isEmpty() || WordData.LANGUAGE_CODES_FROM_KOREAN[selectedLanguageName] == null) {
                    warningMessage = "Language를 선택해주세요!"
                }
                else {
                    warningMessage = ""
                    onAddVoc(title, description, selectedLanguageName)
                }
            }) {
                Text("Add Voc")
            }
        }
    )
}

@Composable
fun VocEditDialog(onDismiss: () -> Unit, currentEditVoc: VocData, onSaveVoc: (String, String) -> Unit, onDeleteVoc: () -> Unit) {
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
        title = { Text(text = "Edit Vocabulary", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                Button(onClick = onDeleteVoc) {
                    Text("Delete Voc")
                }
                Button(onClick = {
                    if(title.isEmpty()) {
                        warningMessage = "Title을 채워주세요!"
                    }
                    else if(description.isEmpty()) {
                        warningMessage = "Description을 채워주세요!"
                    }
                    else {
                        warningMessage = ""
                        onSaveVoc(title, description)
                    }

                }) {
                    Text("Save Voc")
                }
            }
        }
    )
}
