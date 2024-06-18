/*
구현 목록에서 게임 목록 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.b09_wqga.R
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.component.SearchBar2
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.model.GameData
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.nanumFontFamily
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory


@Composable
fun GameListScreen(userId: String, navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var userRepository = UserRepository()
    var userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
    val vocList by vocViewModel.vocList.collectAsState(initial = emptyList())

    LaunchedEffect(userId) {
        userViewModel.fetchName(userId)
        vocViewModel.loadVocs(userId.toInt())

        val userIdInt = userId.toIntOrNull()
        if (userIdInt != null) {
            vocViewModel.loadVocs(userIdInt)  // Correct method call
        }
    }

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

        SearchBar2(
            searchText = userDataViewModel.gameListSearchText.value,
            onSearchTextChanged = { userDataViewModel.gameListSearchText.value = it; userDataViewModel.updateLazyColumnGameList() },
        )


        Spacer(modifier = Modifier.height(16.dp))

        if(gameData != null) {
            Text(
                text = "Recently Played Game",
                fontSize = 20.sp,
                fontFamily = pixelFont1,
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
                vocDataList = vocList,
                onDismiss = {showGameStartDialog = false},
                onPlay = {voc, quizStyle, difficulty, userId ->
                    showGameStartDialog = false
                    userDataViewModel.showBottomNavigationBar.value = false
                    vocViewModel.gameInit(voc, quizStyle, difficulty, userId)
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
                            navController.navigate(Routes.GamePlayScreen_2.route)
//                            navController.navigate("${Routes.GamePlayScreen_2.route}/${voc}/${quizStyle}/${difficulty}/${userId}")
                            {
                                popUpTo(navController.graph.id) {// 백스택 모두 지우기
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                userId = userId
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
            fontFamily = pixelFont2,
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
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.Normal,
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

                    Spacer(modifier = Modifier.width(70.dp))

                }
            }
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Image(painter = painterResource(id = R.drawable.playb),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 80.dp, height = 40.dp)
                        .clickable { onStartClick() }
                )

            }
        }
    }
}

@Composable
fun GameStartDialog(vocDataList : List<Voc>, onDismiss: () -> Unit, onPlay: (String, Int, Int, String) -> Unit, userId: String) {
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
        title = { Text(text = "Game Setting", fontSize = 30.sp, fontFamily = nanumFontFamily, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button_WQGA(width = 80, height = 40, text = "Back", onClickLabel = onDismiss)
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedVocTitle,
                        onValueChange = {},
                        label = { Text("Select Voc", fontFamily = nanumFontFamily, fontWeight = FontWeight.Normal) },
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
                                Log.d("selectedVocUUID", selectedVocUUID)
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
                        label = { Text("Select Quiz Style", fontFamily = nanumFontFamily, fontWeight = FontWeight.Normal) },
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
                            }, text = { Text(text = style, fontFamily = nanumFontFamily, fontWeight = FontWeight.Normal) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedDifficultyName,
                        onValueChange = {},
                        label = { Text("Select Difficulty", fontFamily = nanumFontFamily, fontWeight = FontWeight.Normal) },
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
                            }, text = { Text(text = difficulty, fontFamily = nanumFontFamily, fontWeight = FontWeight.Normal) })
                        }
                    }
                }
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "Play", onClickLabel = {
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
                    onPlay(selectedVocUUID, selectedQuizStyle, selectedDifficulty, userId)
                }
            })
        }
    )
}