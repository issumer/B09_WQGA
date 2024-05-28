package com.example.b09_wqga.screen_test

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextGranularity.Companion.Word
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.b09_wqga.ui.theme.B09_WQGATheme

data class LazyColumnButtonWordTest(val headword: String)

@Composable
fun LazyColumnButtonTest() {
    var wordList by remember { mutableStateOf(List(10) { LazyColumnButtonWordTest("Word $it") }) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Button Clicked") },
            text = {
                val selectedWord = wordList.getOrNull(selectedIndex)?.headword ?: ""
                Text("You clicked button at index: $selectedIndex\nHeadword: $selectedWord")
            },
            confirmButton = {
                Button(onClick = {
                    // Remove the word at the selected index
                    wordList = wordList.filterIndexed { index, _ -> index != selectedIndex }
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn {
        items(wordList) { word ->
            val index = wordList.indexOf(word)
            Button(
                onClick = {
                    selectedIndex = index
                    showDialog = true
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(word.headword)
            }
        }
    }
}

@Preview
@Composable
fun LazyColumnButtonTestPreview() {
    B09_WQGATheme {
        LazyColumnButtonTest()
    }
}