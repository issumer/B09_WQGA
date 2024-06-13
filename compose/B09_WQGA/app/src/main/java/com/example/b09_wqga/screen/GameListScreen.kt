/*
구현 목록에서 게임 목록 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.component.SearchBar
import com.example.b09_wqga.model.GameData
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.VocData
import com.example.b09_wqga.navigation.Routes


@Composable
fun GameListScreen(navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val lazyColumnGameList = userDataViewModel.lazyColumnGameList

    val gameData: GameData? = userDataViewModel.getRecentlyPlayedGame() // 최근에 플레이한 게임

    var currentPlayGameId by rememberSaveable { // 현재 시작하려고 하는 게임의 아이디
        mutableStateOf(-1)
    }

    var showGameStartDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBar(
            searchText = userDataViewModel.gameListSearchText.value,
            onSearchTextChanged = { userDataViewModel.gameListSearchText.value = it; userDataViewModel.updateLazyColumnGameList() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if(gameData != null) {
            Text(
                text = "Recently Played Game",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GameItem(gameData = gameData, onStartClick = {
                showGameStartDialog = true
                currentPlayGameId = gameData.id
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(lazyColumnGameList) { gameData ->
                GameItem(gameData = gameData, onStartClick = {
                    showGameStartDialog = true
                    currentPlayGameId = gameData.id
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if(showGameStartDialog) {
            GameStartDialog(
                vocDataList = userDataViewModel.vocList,
                onDismiss = {showGameStartDialog = false},
                onPlay = {voc, quizStyle, difficulty ->
                    showGameStartDialog = false
                    userDataViewModel.showBottomNavigationBar.value = false
                    userDataViewModel.gameInit(voc, quizStyle, difficulty)
                    when(currentPlayGameId) {
                        1 -> {
                            navController.navigate(Routes.GamePlayScreen_1.route) {
                                popUpTo(navController.graph.id) {// 백스택 모두 지우기
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        2 -> {
                            navController.navigate(Routes.GamePlayScreen_2.route) {
                                popUpTo(navController.graph.id) {// 백스택 모두 지우기
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun GameItem(gameData: GameData, onStartClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = gameData.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = gameData.imageResource),
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = gameData.description,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = "Ranking Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userRanking}", fontSize = 16.sp) // Ranking

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Right Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userRight}", fontSize = 16.sp) // Right

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Wrong Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userWrong}", fontSize = 16.sp) // Wrong

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(onClick = {
                        onStartClick()
                    }) {
                        Text("Play")
                    }
                }
            }
        }
    }
}

// 아직 로직 미구현
@Composable
fun GameStartDialog(vocDataList : List<VocData>, onDismiss: () -> Unit, onPlay: (String, Int, Int) -> Unit) {
    var selectedVocUUID by remember { mutableStateOf("") }
    var selectedVocTitle by remember { mutableStateOf("") }
    var selectedQuizStyle by remember { mutableStateOf(-1) }
    var selectedQuizStyleName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(-1) }
    var selectedDifficultyName by remember { mutableStateOf("") }
    val quizStyles = listOf("완전 랜덤", "틀린 단어 위주", "객관식", "주관식") // Example
    val difficulties = listOf("쉬움", "보통", "어려움") // Example
    var expandedVoc by remember { mutableStateOf(false) }
    var expandedQuizStyle by remember { mutableStateOf(false) }
    var expandedDifficulty by remember { mutableStateOf(false) }

    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Game Setting", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDismiss) {
                    Text("Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedVocTitle,
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
                        vocDataList.forEach { vocData ->
                            DropdownMenuItem(onClick = {
                                selectedVocUUID = vocData.uuid
                                selectedVocTitle = vocData.title
                                expandedVoc = false
                            }, text = { Text(text = vocData.title) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedQuizStyleName,
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
                        quizStyles.forEachIndexed { index, style ->
                            DropdownMenuItem(onClick = {
                                selectedQuizStyle = index
                                selectedQuizStyleName = style
                                expandedQuizStyle = false
                            }, text = { Text(text = style) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedDifficultyName,
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
                        difficulties.forEachIndexed { index, difficulty ->
                            DropdownMenuItem(onClick = {
                                selectedDifficulty = index
                                selectedDifficultyName = difficulty
                                expandedDifficulty = false
                            }, text = { Text(text = difficulty) })
                        }
                    }
                }
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Button(onClick = {
                var canPlay = true
                if(selectedVocUUID.isEmpty()) {
                    warningMessage = "Select Voc를 채워주세요!"
                    canPlay = false
                }
                if(selectedQuizStyleName.isEmpty() && canPlay) {
                    warningMessage = "Select Quiz Style를 채워주세요!"
                    canPlay = false
                }
                if(selectedDifficultyName.isEmpty() && canPlay) {
                    warningMessage = "Select Difficulty를 채워주세요!"
                    canPlay = false
                }

                if(canPlay) {
                    onPlay(selectedVocUUID, selectedQuizStyle, selectedDifficulty)
                }
            }) {
                Text("Play")
            }
        }
    )
}
