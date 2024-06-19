package com.example.b09_wqga.screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.component.WordQuiz
import com.example.b09_wqga.database.Played
import com.example.b09_wqga.navigation.Routes
import com.example.b09_wqga.repository.PlayedRepository
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.nanumFontFamily
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.MiscViewModel
import com.example.b09_wqga.viewmodel.PlayedViewModel
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.PlayedViewModelFactory
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RPGEnemy(
    val x: Float = 0.0f,
    val y: Float = 0.0f,
    val width: Float,
    val height: Float,
    var health: Int,
    val maxHealth: Int,
    val damage: Int,
    val score: Int,
    val difficultyModifier: Double,
    val availableStage: Int,
    var painter: Painter? = null,
    val painterResourceId: Int
)

data class RPGPlayer(
    var x: Float,
    var y: Float,
    var health: Int,
    val maxHealth: Int,
    val damage: Int
)

sealed class RPGAttributes {
    companion object {
        val RPGEnemies = listOf(
            RPGEnemy(width = 60.0f, height = 60.0f, health = 20, maxHealth = 20, damage = 5, score = 20, difficultyModifier = 0.1, availableStage = 0, painterResourceId = R.drawable.enemy1),
            RPGEnemy(width = 60.0f, height = 60.0f, health = 26, maxHealth = 26, damage = 8, score = 30, difficultyModifier = 0.12, availableStage = 3, painterResourceId = R.drawable.enemy2),
            RPGEnemy(width = 60.0f, height = 60.0f, health = 40, maxHealth = 40, damage = 6, score = 60, difficultyModifier = 0.15, availableStage = 5, painterResourceId = R.drawable.enemy3)
        )
        // 여러 배경 추가
        val backgroundsID = listOf(
            R.drawable.background1
            //R.drawable.background2
            //R.drawable.background3
            //...
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamePlayScreen_1(navController: NavHostController, vocId: Int, userId: Int, currentPlayGameId: Int) {
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(VocRepository()))
    val miscViewModel: MiscViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val wordList by vocViewModel.wordList.collectAsState()
    var quiz by remember { mutableStateOf(vocViewModel.createQuiz(vocId)) }
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var player by remember { mutableStateOf(RPGPlayer(x = 0.0f, y = 0.0f, health = 100, maxHealth = 100, damage = 12)) }
    var enemies by remember { mutableStateOf(mutableListOf<RPGEnemy>()) }
    var damaged by remember { mutableStateOf(false) }
    var enemydamaged by remember { mutableStateOf(false) }
    var selectedEnemyIndex by remember { mutableStateOf(-1) }
    var playerTurn by remember { mutableStateOf(true) }
    var playerQuizPaused by remember { mutableStateOf(false) }
    var playerQuizResult by remember { mutableStateOf(false) }
    var playerBlockSkill by remember { mutableStateOf(false) }
    var stage by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var rightCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var centerMessage by remember { mutableStateOf("") }
    var damageMessage by remember { mutableStateOf("") }
    var damagePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var gameOver by remember { mutableStateOf(false) }
    var gamePaused by remember { mutableStateOf(false) }
    var showMenuDialog by rememberSaveable { mutableStateOf<Boolean>(false) }
    var showEndDialog by rememberSaveable { mutableStateOf<Boolean>(false) }

    var recomposeKey by remember { mutableStateOf(false) }
    var showAttackSkills by remember { mutableStateOf(false) }
    var showDefenseSkills by remember { mutableStateOf(false) }
    val difficulty = vocViewModel.gameDifficulty.collectAsState()

    val effect1_vec: Painter = painterResource(id = R.drawable.effect1)
    val effect2_vec: Painter = painterResource(id = R.drawable.effect2)
    val arrow: Painter = painterResource(id = R.drawable.arrow)
    RPGAttributes.RPGEnemies[0].painter = painterResource(id = R.drawable.enemy1)
    RPGAttributes.RPGEnemies[1].painter = painterResource(id = R.drawable.enemy2)
    RPGAttributes.RPGEnemies[2].painter = painterResource(id = R.drawable.enemy3)
    val player_vec: Painter = painterResource(id = R.drawable.player)
    val yourturn: Painter = painterResource(id = R.drawable.yourturn)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(vocId) {
        vocViewModel.loadWordsByVocId(vocId)
    }

    LaunchedEffect(wordList) {
        if (wordList.isNotEmpty()) {
            quiz = vocViewModel.createQuiz(vocId)
        }
    }

    // 초기화 코드 (스테이지 1)
    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            player = player.copy(
                x = canvasSize.width / 6.0f,
                y = canvasSize.height / 1.5f,
            )

            enemies = mutableListOf<RPGEnemy>()

            for (i in 0..2) {
                enemies.add(RPGAttributes.RPGEnemies[0].copy(
                    x = if (i == 1) canvasSize.width * 3 / 5.0f else canvasSize.width * 3 / 5.0f + 170,
                    y = if (i == 1) canvasSize.height / 10.0f - (i * 120) + 700 else canvasSize.height / 10.0f - (i * 130) + 700,
                    health = (RPGAttributes.RPGEnemies[0].health * (1 + RPGAttributes.RPGEnemies[0].difficultyModifier * difficulty.value)).roundToInt(),
                    maxHealth = (RPGAttributes.RPGEnemies[0].maxHealth * (1 + RPGAttributes.RPGEnemies[0].difficultyModifier * difficulty.value)).roundToInt(),
                    damage = (RPGAttributes.RPGEnemies[0].damage * (1 + RPGAttributes.RPGEnemies[0].difficultyModifier * difficulty.value)).roundToInt()
                ))
            }
        }
    }

    fun setStage() {
        val enemyCountMax = when (stage) {
            in 0..3 -> 3
            in 4..6 -> 4
            else -> 5
        }

        val possibleEnemyPool = RPGAttributes.RPGEnemies.filter { it.availableStage <= stage }
        enemies = mutableListOf()

        for (i in 0 until enemyCountMax) {
            val randomEnemyIndex = Random.nextInt(possibleEnemyPool.size)
            val randomEnemy = possibleEnemyPool[randomEnemyIndex]

            enemies.add(randomEnemy.copy(
                x = if (enemyCountMax == 3) {
                    if (i == 1) canvasSize.width * 3 / 5.0f else canvasSize.width * 3 / 5.0f + 170
                } else if (enemyCountMax == 4) {
                    if (i == 0 || i == 1) canvasSize.width * 3 / 5.0f else canvasSize.width * 3 / 5.0f + 200
                } else {
                    if (i in 0..1) canvasSize.width * 3 / 8.0f + 40 else if (i == 2) canvasSize.width * 3 / 8.0f + 240 else canvasSize.width * 3 / 8.0f + 440
                },
                y = if (enemyCountMax == 3) {
                    if (i == 1) canvasSize.height / 10.0f - (i * 120) + 700 else canvasSize.height / 10.0f - (i * 130) + 700
                } else if (enemyCountMax == 4) {
                    if (i == 0 || i == 2) canvasSize.height / 10.0f + 700 else canvasSize.height / 10.0f + 500
                } else {
                    if (i == 0 || i == 3) canvasSize.height / 10.0f + 700 else if (i == 1 || i == 4) canvasSize.height / 10.0f + 400 else canvasSize.height / 10.0f + 550
                },
                health = (randomEnemy.health * (1 + randomEnemy.difficultyModifier * difficulty.value)).roundToInt(),
                maxHealth = (randomEnemy.maxHealth * (1 + randomEnemy.difficultyModifier * difficulty.value)).roundToInt(),
                damage = (randomEnemy.damage * (1 + randomEnemy.difficultyModifier * difficulty.value)).roundToInt()
            ))
        }
    }

    fun nextStage() {
        stage += 1
        player.health = player.maxHealth
        setStage()
    }

    suspend fun showDamageMessage() {
        delay(1000L)
        damageMessage = ""
        damaged = false
        delay(200L)
    }

    suspend fun showDamageEffect_1() {
        damaged = true
    }

    suspend fun showDamageEffect_2() {
        enemydamaged = true
        delay(400L)
        enemydamaged = false
    }

    suspend fun performEnemyAttack() {
        if (enemies.isNotEmpty()) {
            enemies.forEach { enemy ->
                delay(400L)
                var damage = Random.nextInt(enemy.damage - 3, enemy.damage + 3)
                if (playerBlockSkill) {
                    damage = (damage / 2.0).roundToInt()
                }
                player.health -= damage
                damageMessage = "-$damage"
                damagePosition = Offset(player.x + 70.0f, player.y - 50.0f)
                showDamageEffect_1()
                showDamageMessage()
            }
        }
        delay(400L)
        playerBlockSkill = false

        if (player.health <= 0) {
            gameOver = true
            delay(3000L)
            showEndDialog = true

        } else if (enemies.isEmpty()) {
            delay(2000L)
            nextStage()
        }

        playerQuizPaused = false
        playerTurn = true
    }

    fun performSkill(skillNum: Int) {
        if (playerTurn) {
            when (skillNum) {
                0 -> { // 찌르기
                    if (selectedEnemyIndex == -1 || !(selectedEnemyIndex in 0 until enemies.size)) { // 아무 적도 선택하지 않음
                        selectedEnemyIndex = Random.nextInt(enemies.size)
                    }

                    val damage = Random.nextInt(player.damage - 5, player.damage + 10) // 플레이어 공격력 -10 ~ +10 사이 데미지
                    enemies[selectedEnemyIndex].health -= damage
                    damageMessage = "-$damage"
                    damagePosition = Offset(enemies[selectedEnemyIndex].x + enemies[selectedEnemyIndex].width / 2 + 30, enemies[selectedEnemyIndex].y - 25)
                    enemydamaged = true
                    if (enemies[selectedEnemyIndex].health <= 0) {
                        score += enemies[selectedEnemyIndex].score
                        enemies = enemies.filterIndexed { index, _ -> index != selectedEnemyIndex }.toMutableList()
                        selectedEnemyIndex = -1
                    }

                    selectedEnemyIndex = -1
                    playerTurn = false
                }
                1 -> { // 체력 회복
                    player.health = if (player.health + 50 >= player.maxHealth) player.maxHealth else player.health + 50
                    playerTurn = false
                }
            }
            playerQuizResult = false
            showAttackSkills = false
            showDefenseSkills = false
            playerQuizPaused = true
        }
    }

    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(16L)
            if (!playerTurn) {
                showDamageMessage()
                performEnemyAttack()
                quiz = vocViewModel.createQuiz(vocId)
                recomposeKey = !recomposeKey // 강제 recompose
            }
        }
    }

    LaunchedEffect(enemydamaged) {
        delay(400L)
        enemydamaged = false
    }

    val playedViewModel: PlayedViewModel = viewModel(factory = PlayedViewModelFactory(PlayedRepository()))

    var isPlayedUpdated by remember { mutableStateOf(false) }

    fun updatePlayedData(score: Int, rightCount: Int, wrongCount: Int) {
        if (!isPlayedUpdated) {
            playedViewModel.getAllPlayedByUserId(userId) { playedList ->
                val currentGameId = currentPlayGameId
                val currentPlayed = playedList.find { it.game_id == currentGameId }
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val played = if (currentPlayed != null) {
                    currentPlayed.copy(
                        best_score = maxOf(currentPlayed.best_score, score),
                        right = currentPlayed.right + rightCount,
                        wrong = currentPlayed.wrong + wrongCount,
                        play_count = currentPlayed.play_count + 1,
                        play_date = today
                    )
                } else {
                    Played(
                        user_id = userId,
                        game_id = currentGameId,
                        best_score = score,
                        right = rightCount,
                        wrong = wrongCount,
                        play_count = 1,
                        play_date = today
                    )
                }

                playedViewModel.addOrUpdatePlayed(played)
                isPlayedUpdated = true
            }
        }
    }

    if (gameOver) {
        updatePlayedData(score, rightCount, wrongCount)
    }

    if (showEndDialog) {
        updatePlayedData(score, rightCount, wrongCount)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
        ) {

            Image(
                painter = painterResource(R.drawable.background1),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(700.dp)
            )

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val clickedEnemyIndex = enemies.indexOfFirst { enemy ->
                                offset.x >= enemy.x + 50 && offset.x <= enemy.x + 150 &&
                                        offset.y >= enemy.y + 10 && offset.y <= enemy.y + 150
                            }
                            if (clickedEnemyIndex != -1 && playerTurn) {
                                selectedEnemyIndex = clickedEnemyIndex
                            }
                        }
                    }
                    .onSizeChanged { size ->
                        canvasSize = size
                    }
            ) {
                drawIntoCanvas {

                    if (playerTurn) {
                        with(yourturn) {
                            translate(left = 0f, top = 30f) {
                                draw(size = Size(150.dp.toPx(), 80.dp.toPx()))
                            }
                        }
                    }

                    with(player_vec) {
                        translate(left = player.x, top = player.y) {
                            draw(size = Size(50.dp.toPx(), 50.dp.toPx()))
                        }
                    }

                    if (damaged) {
                        with(effect1_vec) {
                            val damagel1 = Random.nextInt(0, 60)
                            val damagel2 = Random.nextInt(-30, 10)
                            translate(left = player.x + damagel1, top = player.y + damagel2) {
                                draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                            }
                        }
                    }


                    enemies.forEachIndexed { index, enemy ->

                        if (index == selectedEnemyIndex) {
                            with(arrow) {
                                translate(left = enemy.x + 50, top = enemy.y - 90) {
                                    draw(size = Size(30.dp.toPx(), 30.dp.toPx()))
                                }
                            }
                        }
                        with(enemy.painter!!) {
                            translate(left = enemy.x, top = enemy.y) {
                                draw(size = Size(60.dp.toPx(), 60.dp.toPx()))
                            }
                        }
                        drawRect(   //테두리
                            color = Color.Black,
                            topLeft = Offset(enemy.x + 23, enemy.y + enemy.height - 78),
                            size = Size(enemy.maxHealth * 6f + 14, 10f + 14)
                        )
                        drawRect(
                            color = Color.Gray,
                            topLeft = Offset(enemy.x + 30, enemy.y + enemy.height - 71),
                            size = Size(enemy.maxHealth * 6f, 10f)
                        )
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(enemy.x + 30, enemy.y + enemy.height - 71),
                            size = Size(enemy.health * 6f, 10f)
                        )

                        if (index == selectedEnemyIndex && enemydamaged) {
                            with(effect2_vec) {
                                translate(left = enemy.x - 50, top = enemy.y + 20) {
                                    draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                                }
                            }
                        }
                    }
                    drawRect(   //테두리
                        color = Color.Black,
                        topLeft = Offset(player.x - 16, (player.y - 30) - 7),
                        size = Size(player.maxHealth * 1.5f + 14, 10f + 14)
                    )
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(player.x - 9, (player.y - 30)),
                        size = Size(player.maxHealth * 1.5f, 10f)
                    )
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(player.x - 9, (player.y - 30)),
                        size = Size(player.health * 1.5f, 10f)
                    )


                    if (damageMessage.isNotEmpty()) {
                        drawContext.canvas.nativeCanvas.drawText(
                            damageMessage,
                            damagePosition.x,
                            damagePosition.y,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.RED
                                textSize = 40f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp, top = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pausebutton_2),
                    contentDescription = "menu",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable { showMenuDialog = true }
                        .size(width = 70.dp, height = 30.dp)
                )
            }

            if (gameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(R.drawable.gameover),
                        contentDescription = "gameover",
                        modifier = Modifier.size(150.dp))
                }
            }

            if (enemies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(R.drawable.nextstage),
                        contentDescription = "gameover",
                        modifier = Modifier.size(150.dp))
                }
            }

            if (centerMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(centerMessage, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
        Box() {
            Image(
                painter = painterResource(id = R.drawable.bar),
                contentDescription = "status bar",
                contentScale = ContentScale.FillWidth
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (playerQuizResult) {
                    Image(
                        painter = painterResource(id = R.drawable.sword),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                performSkill(0)
                            }
                            .size(80.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.potion),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                performSkill(1)
                            }
                            .size(80.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.skipbutton),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                if (playerTurn) {
                                    showAttackSkills = false
                                    showDefenseSkills = false
                                    playerTurn = false
                                    playerQuizPaused = true
                                }
                            }
                            .size(80.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        WordQuiz(quiz, recomposeKey = recomposeKey, playerQuizPaused, onSubmit = { quizResult ->
            playerQuizResult = quizResult
            Log.d("GamePlayScreen_1", "Quiz result: $quizResult")
            if (playerQuizResult) {
                rightCount += 1
                playerTurn = true
            } else {
                wrongCount += 1
            }
        })

        if (showMenuDialog) {
            GameMenuDialog(
                onDismiss = {
                    if (!gameOver) {
                        showMenuDialog = false
                    }
                },
                onExitGame = {
                    miscViewModel.showBottomNavigationBar.value = true
                    navController.navigate(Routes.GameListScreen.route) {
                        popUpTo(navController.graph.id) {// 백스택 모두 지우기
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                score = score,
                rightCount = rightCount,
                wrongCount = wrongCount
            )
        }

        if (showEndDialog) {
            GameEndDialog(
                onDismiss = {
                    if (!gameOver) {
                        showEndDialog = false
                    }
                },
                onExitGame = {
                    navController.navigate(Routes.GameListScreen.route) {
                        popUpTo(navController.graph.id) {// 백스택 모두 지우기
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                score = score,
                rightCount = rightCount,
                wrongCount = wrongCount
            )
        }
    }
}

@Composable
fun GameMenuDialog(onDismiss: () -> Unit, onExitGame: () -> Unit, score: Int, rightCount: Int, wrongCount: Int) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PAUSE",
                    fontSize = 27.sp,
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(5.dp))
                Text("점수: $score", fontFamily = pixelFont2, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.height(3.dp))
                Text("맞은 개수: $rightCount", fontFamily = pixelFont2, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.height(3.dp))
                Text("틀린 개수: $wrongCount", fontFamily = pixelFont2, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.height(25.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.width(30.dp))
                    Button_WQGA(width = 80, height = 40, text = "Back", onClickLabel = onDismiss)
                    Spacer(modifier = Modifier.width(50.dp))
                    Button_WQGA(width = 80, height = 40, text = "Exit", onClickLabel = onExitGame)
                }
            }
        },
        confirmButton = {

        }
    )
}

@Composable
fun GameEndDialog(onDismiss: () -> Unit, onExitGame: () -> Unit, score: Int, rightCount: Int, wrongCount: Int) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RESULT",
                    fontSize = 27.sp,
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(5.dp))
                Text("점수: $score", fontSize = 15.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Normal, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(3.dp))
                Text("맞은 개수: $rightCount", fontSize = 15.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Normal, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(3.dp))
                Text("틀린 개수: $wrongCount", fontSize = 15.sp, fontFamily = pixelFont2, fontWeight = FontWeight.Normal, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(25.dp))

                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Button_WQGA(width = 80, height = 40, text = "Exit", onClickLabel = onExitGame)
                }
            }
        },
        confirmButton = {

        }
    )
}
