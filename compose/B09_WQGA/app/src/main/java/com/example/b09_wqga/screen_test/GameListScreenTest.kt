package com.example.b09_wqga.screen_test

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.Color
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme

data class GameTest(
    val title: String,
    val description: String,
    val ranking: Int,
    val right: Int,
    val wrong: Int,
    val imageResource: Int
)

@Composable
fun GameListScreenTest() {
    var searchText by remember { mutableStateOf("") }
    val gameList = listOf(
        GameTest("Game 1", "Description 1", 1, 10, 2, R.drawable.ic_launcher_foreground),
        GameTest("Game 2", "Description 2", 2, 15, 3, R.drawable.ic_launcher_foreground),
        // Add more items here
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBarTest(
            searchText = searchText,
            onSearchTextChanged = { searchText = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recently Played Game",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        GameItemTest(game = gameList[0])

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(gameList) { game ->
                GameItemTest(game = game)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SearchBarTest(searchText: String, onSearchTextChanged: (String) -> Unit) {
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
    }
}

@Composable
fun GameItemTest(game: GameTest) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = game.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = game.imageResource),
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = game.description,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = "Ranking Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${game.ranking}", fontSize = 16.sp) // Ranking

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Right Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${game.right}", fontSize = 16.sp) // Right

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Wrong Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${game.wrong}", fontSize = 16.sp) // Wrong

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(onClick = { /*TODO*/ }) {
                        Text("Play")
                    }
                }
            }
        }
    }
}

@Composable
fun GameStartDialogTest(onDismiss: () -> Unit, onPlay: (String, String, String) -> Unit) {
    var selectedVoc by remember { mutableStateOf("") }
    var selectedQuizStyle by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("") }
    val vocs = listOf("Vocabulary 1", "Vocabulary 2", "Vocabulary 3") // Example vocabularies
    val quizStyles = listOf("Multiple Choice", "Fill in the Blanks") // Example quiz styles
    val difficulties = listOf("Easy", "Medium", "Hard") // Example difficulties
    var expandedVoc by remember { mutableStateOf(false) }
    var expandedQuizStyle by remember { mutableStateOf(false) }
    var expandedDifficulty by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Start Game", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedVoc,
                        onValueChange = {},
                        label = { Text("Select Voc") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedVoc = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedVoc,
                        onDismissRequest = { expandedVoc = false }
                    ) {
                        vocs.forEach { voc ->
                            DropdownMenuItem(onClick = {
                                selectedVoc = voc
                                expandedVoc = false
                            }, text = {Text(text = voc)})
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedQuizStyle,
                        onValueChange = {},
                        label = { Text("Select Quiz Style") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedQuizStyle = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedQuizStyle,
                        onDismissRequest = { expandedQuizStyle = false }
                    ) {
                        quizStyles.forEach { style ->
                            DropdownMenuItem(onClick = {
                                selectedQuizStyle = style
                                expandedQuizStyle = false
                            }, text = {Text(text = style)})
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedDifficulty,
                        onValueChange = {},
                        label = { Text("Select Difficulty") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedDifficulty = true }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedDifficulty,
                        onDismissRequest = { expandedDifficulty = false }
                    ) {
                        difficulties.forEach { difficulty ->
                            DropdownMenuItem(onClick = {
                                selectedDifficulty = difficulty
                                expandedDifficulty = false
                            }, text = {Text(text = difficulty)})
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onPlay(selectedVoc, selectedQuizStyle, selectedDifficulty) }) {
                Text("Play")
            }
        }
    )
}


@Composable
fun GameMenuDialogTest(onDismiss: () -> Unit, onExitGame: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Game Menu", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onExitGame) {
                    Text("Exit Game")
                }
            }
        },
        confirmButton = {

        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameListScreenTestPreview() {
    B09_WQGATheme {
        GameListScreenTest()
    }
}

@Preview(showBackground = true)
@Composable
fun GameStartDialogTestPreview() {
    B09_WQGATheme {
        GameStartDialogTest(onDismiss = { /*TODO*/ }, onPlay = { _, _, _ -> /*TODO*/ })
    }
}

@Preview(showBackground = true)
@Composable
fun GameMenuDialogTestPreview() {
    B09_WQGATheme {
        GameMenuDialogTest(onDismiss = { /*TODO*/ }, onExitGame = { /*TODO*/ })
    }
}
