package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.b09_wqga.database.Game
import com.example.b09_wqga.database.Voc
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.repository.GameRepository
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.nanumFontFamily
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.GameViewModel
import com.example.b09_wqga.viewmodel.MiscViewModel
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.GameViewModelFactory
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory

@Composable
fun GameListScreen(navController: NavHostController, userId: Int) {
    val miscViewModel: MiscViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val gameRepository = GameRepository()
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(gameRepository))

    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))

    var showGameStartDialog by rememberSaveable { mutableStateOf(false) }
    var currentPlayGameId by rememberSaveable { mutableStateOf(-1) }

    val gameList by gameViewModel.gameList.collectAsState()
    val vocList by vocViewModel.vocList.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        gameViewModel.loadAllGames()
        vocViewModel.loadVocs(userId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        SearchBar2(
            searchText = "", // 검색 텍스트 관리
            onSearchTextChanged = {} // 검색 텍스트 변경 로직 추가 필요
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(gameList) { game ->
                GameItem(game = game, onStartClick = {
                    showGameStartDialog = true
                    currentPlayGameId = game.game_id
                    },
                    imagesc = if(game.game_id == 1){
                        R.drawable.game1
                    }
                    else{
                        R.drawable.game2
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showGameStartDialog) {
            GameStartDialog(
                vocList = vocList,
                onDismiss = { showGameStartDialog = false },
                onPlay = { voc, quizStyle, difficulty ->
                    showGameStartDialog = false
                    vocViewModel.quizStyle.value = quizStyle
                    vocViewModel.gameDifficulty.value = difficulty
                    miscViewModel.showBottomNavigationBar.value = false
                    when (currentPlayGameId) {
                        1 -> {
                            navController.navigate("GamePlayScreen_1/${voc.voc_id}") {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        2 -> {
                            navController.navigate("GamePlayScreen_2/${voc.voc_id}") {
                                popUpTo(navController.graph.id) {
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
fun GameItem(game: Game, onStartClick: () -> Unit, imagesc: Int) {
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(90.dp)
            .background(Color.LightGray)
            .padding(all = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {


            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = imagesc), // Placeholder 이미지, 필요시 변경
                    contentDescription = "Game Icon",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier= Modifier.padding(top = 2.dp)) {
                    Text(
                        text = game.gamename,
                        fontSize = 20.sp,
                        fontFamily = pixelFont2,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = game.description,
                        fontSize = 16.sp,
                        fontFamily = pixelFont2,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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
}

@Composable
fun GameStartDialog(vocList: List<Voc>, onDismiss: () -> Unit, onPlay: (Voc, Int, Int) -> Unit) {
    var selectedVocTitle by remember { mutableStateOf("") }
    var selectedVoc: Voc? = null
    var selectedQuizStyle by remember { mutableStateOf(-1) }
    var selectedQuizStyleName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(-1) }
    var selectedDifficultyName by remember { mutableStateOf("") }
    val quizStyles = listOf("완전 랜덤", "틀린 단어 위주", "객관식", "주관식")
    val difficulties = listOf("쉬움", "보통", "어려움")
    var expandedVoc by remember { mutableStateOf(false) }
    var expandedQuizStyle by remember { mutableStateOf(false) }
    var expandedDifficulty by remember { mutableStateOf(false) }

    var warningMessage by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Game Setting", fontSize = 30.sp, fontFamily = pixelFont2, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button_WQGA(width = 80, height = 40, text = "Back", onClickLabel = onDismiss)
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedVocTitle,
                        onValueChange = {},
                        label = { Text("Select Vocabulary", fontFamily = pixelFont2, fontWeight = FontWeight.Normal) },
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
                        vocList.forEach { voc ->
                            DropdownMenuItem(onClick = {
                                selectedVoc = voc
                                selectedVocTitle = voc.title
                                expandedVoc = false
                            }, text = { Text(text = voc.title, fontFamily = pixelFont2) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedQuizStyleName,
                        onValueChange = {},
                        label = { Text("Select Quiz Style", fontFamily = pixelFont2, fontWeight = FontWeight.Normal) },
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
                            }, text = { Text(text = style, fontFamily = pixelFont2, fontWeight = FontWeight.Normal) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    OutlinedTextField(
                        value = selectedDifficultyName,
                        onValueChange = {},
                        label = { Text("Select Difficulty", fontFamily = pixelFont2, fontWeight = FontWeight.Normal) },
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
                            }, text = { Text(text = difficulty, fontFamily = pixelFont2, fontWeight = FontWeight.Normal) })
                        }
                    }
                }
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "Play", onClickLabel = {
                var canPlay = true
                if (selectedVoc == null) {
                    warningMessage = "Select Voc를 채워주세요!"
                    canPlay = false
                }
                if (selectedQuizStyleName.isEmpty() && canPlay) {
                    warningMessage = "Select Quiz Style를 채워주세요!"
                    canPlay = false
                }
                if (selectedDifficultyName.isEmpty() && canPlay) {
                    warningMessage = "Select Difficulty를 채워주세요!"
                    canPlay = false
                }

                if (canPlay) {
                    onPlay(selectedVoc!!, selectedQuizStyle, selectedDifficulty)
                }
            })
        }
    )
}
