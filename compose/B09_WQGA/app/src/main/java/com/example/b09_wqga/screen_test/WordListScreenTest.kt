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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextGranularity.Companion.Word
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme

data class WordTest(
    val headword: String,
    val meanings: List<String>
)

@Composable
fun WordListScreenTest() {
    var searchText by remember { mutableStateOf("") }
    var sortBasedOn by remember { mutableStateOf("Date") }
    var sortOrder by remember { mutableStateOf("Asc") }
    val words = listOf(
        WordTest("Word 1", listOf("Meaning 1.1", "Meaning 1.2", "Meaning 1.3")),
        WordTest("Word 2", listOf("Meaning 2.1", "Meaning 2.2")),
        // Add more items here
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBarTest2(
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            onAddWordClick = { /*TODO*/ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SortDropdownMenuTest(
                label = "Sort based on",
                selectedOption = sortBasedOn,
                options = listOf("Date", "Alphabetical"),
                onOptionSelected = { sortBasedOn = it }
            )

            SortDropdownMenuTest(
                label = "Sort Asc / Desc",
                selectedOption = sortOrder,
                options = listOf("Asc", "Desc"),
                onOptionSelected = { sortOrder = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(words) { word ->
                WordItemTest(word = word)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBarTest2(searchText: String, onSearchTextChanged: (String) -> Unit, onAddWordClick: () -> Unit) {
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

        Button(onClick = onAddWordClick) {
            Text("Add Word")
        }
    }
}

@Composable
fun SortDropdownMenuTest(label: String, selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

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
fun WordItemTest(word: WordTest) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = word.headword,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        word.meanings.forEach { meaning ->
            Text(text = meaning, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Right")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Wrong")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { /*TODO*/ }) {
                    Text("TTS")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { /*TODO*/ }) {
                    Text("Delete Word")
                }
            }
        }
    }
}

@Composable
fun WordAddDialogTest(onDismiss: () -> Unit, onAddVoc: (String, List<String>) -> Unit) {
    var headword by remember { mutableStateOf("") }
    var meanings = remember { mutableStateListOf("", "", "", "", "") }

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
                    Button(onClick = { /*TODO*/ }) {
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
            }
        },
        confirmButton = {
            Button(onClick = { onAddVoc(headword, meanings) }) {
                Text("Add Voc")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WordListScreenPreview() {
    B09_WQGATheme {
        WordListScreenTest()
    }
}

@Preview(showBackground = true)
@Composable
fun WordAddDialogPreview() {
    B09_WQGATheme {
        WordAddDialogTest(onDismiss = { /*TODO*/ }, onAddVoc = { _, _ -> /*TODO*/ })
    }
}