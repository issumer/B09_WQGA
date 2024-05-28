package com.example.b09_wqga.screen_test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.Color
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme


data class VocabularyTest(
    val title: String,
    val description: String,
    val count: Int,
    val language: String
)

@Composable
fun VocListScreenTest() {
    var searchText by remember { mutableStateOf("") }
    val vocabList = listOf(
        VocabularyTest("Title 1", "Description 1", 10, "English"),
        VocabularyTest("Title 2", "Description 2", 5, "Spanish"),
        // Add more items here
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBarTest(
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            onAddVocClick = { /*TODO*/ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(vocabList) { voc ->
                VocItem(vocabulary = voc)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBarTest(searchText: String, onSearchTextChanged: (String) -> Unit, onAddVocClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChanged,
            label = { Text("Enter Search") },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = onAddVocClick) {
            Text("Add") // Add Voc
        }
    }
}

@Composable
fun VocItem(vocabulary: VocabularyTest) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = vocabulary.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = vocabulary.description,
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
                    modifier = Modifier.size(24.dp), // Set the size here
                    tint = Color.Blue,
                    contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Count: ${vocabulary.count}")

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Language,
                    modifier = Modifier.size(24.dp), // Set the size here
                    tint = Color.Blue,
                    contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = vocabulary.language)
            }

            //Spacer(modifier = Modifier.width(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { /*TODO*/ }) {
                    Text("Edit") // Edit Voc
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { /*TODO*/ }) {
                    Text("Enter") // Enter Voc
                }
            }
        }
    }
}

@Composable
fun VocAddDialogTest(onDismiss: () -> Unit, onAddVoc: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("") }
    val languages = listOf("English", "Spanish", "French") // Example languages
    var expanded by remember { mutableStateOf(false) }

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
                Box {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        label = { Text("Select Language") },
                        modifier = Modifier.fillMaxWidth(),
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
                        languages.forEach { language ->
                            DropdownMenuItem(
                                onClick = {
                                selectedLanguage = language
                                expanded = false
                            },
                                text = { Text(language) })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAddVoc(title, description, selectedLanguage) }) {
                Text("Add Voc")
            }
        }
    )
}

@Composable
fun VocEditDialogTest(onDismiss: () -> Unit, onSaveVoc: (String, String) -> Unit, onDeleteVoc: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDeleteVoc) {
                    Text("Delete Voc")
                }
                Button(onClick = { onSaveVoc(title, description) }) {
                    Text("Save Voc")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun VocListScreenTestPreview() {
    B09_WQGATheme {
        VocListScreenTest()
    }
}

@Preview(showBackground = true)
@Composable
fun VocAddDialogTestPreview() {
    B09_WQGATheme {
        VocAddDialogTest(onDismiss = { /*TODO*/ }, onAddVoc = { _, _, _ -> /*TODO*/ })
    }
}

@Preview(showBackground = true)
@Composable
fun VocEditDialogPreview() {
    B09_WQGATheme {
        VocEditDialogTest(onDismiss = { /*TODO*/ }, onSaveVoc = { _, _ -> /*TODO*/ }, onDeleteVoc = { /*TODO*/ })
    }
}