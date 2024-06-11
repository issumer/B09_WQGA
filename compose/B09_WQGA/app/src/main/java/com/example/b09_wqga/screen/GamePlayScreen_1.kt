/*
구현 목록에서 게임 1 (턴제 RPG 게임) 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.WordQuiz
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes
import kotlinx.coroutines.delay
import kotlin.random.Random

data class RPGEnemy(var x: Float, var y: Float, var width: Float, var height: Float, var health: Int, var maxHealth: Int)

data class RPGPlayer(var x: Float, var y: Float, var radius: Float, var health: Int, val maxHealth: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GamePlayScreen_1(navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var player by remember { mutableStateOf(RPGPlayer(x = 0f, y = 0f, radius = 30f, health = 100, maxHealth = 100)) }
    var enemies by remember { mutableStateOf(emptyList<RPGEnemy>()) }
    var position by remember { mutableStateOf(-1) }
    var damaged by remember { mutableStateOf(false) }
    var enemydamaged by remember { mutableStateOf(false) }
    var selectedEnemyIndex by remember { mutableStateOf(-1) }
    var message by remember { mutableStateOf("") }
    var playerTurn by remember { mutableStateOf(true) }
    var playerQuizResult by remember { mutableStateOf(false) }
    var damageMessage by remember { mutableStateOf("") }
    var damagePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var gameOver by remember { mutableStateOf(false) } // 게임 종료 여부
    var gamePaused by remember { mutableStateOf(false) } // 게임 잠깐 멈춤 여부 (메뉴 열기 등의 이유로)
    var showMenuDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    var recomposeKey by remember { mutableStateOf(false) }

    val effect1_vec: Painter = painterResource(id = R.drawable.effect1)
    val effect2_vec: Painter = painterResource(id = R.drawable.effect2)
    val arrow: Painter = painterResource(id = R.drawable.arrow)
    val enemy1_vec: Painter = painterResource(id = R.drawable.enemy1)
    val enemy2_vec: Painter = painterResource(id = R.drawable.enemy2)
    val enemy3_vec: Painter = painterResource(id = R.drawable.enemy3)
    val player_vec: Painter = painterResource(id = R.drawable.player)

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            player = player.copy(
                x = canvasSize.width / 6f,
                y = canvasSize.height / 1.5f,
            )

            enemies = List(3) { index ->
                if(index == 1){
                    RPGEnemy(
                        x = canvasSize.width * 3 / 5f,
                        y = canvasSize.height / 10f - (index * 120) + 700,
                        width = 60f,
                        height = 60f,
                        health = 20,
                        maxHealth = 20
                    )
                }
                else {
                    RPGEnemy(
                        x = canvasSize.width * 3 / 5f + 170,
                        y = canvasSize.height / 10f - (index * 130) + 700,
                        width = 60f,
                        height = 60f,
                        health = 20,
                        maxHealth = 20
                    )
                }
            }
        }
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
        delay(500L)
        enemydamaged = false
    }

    suspend fun performEnemyAttack() {
        if (enemies.isNotEmpty()) {
            enemies.forEach { enemy ->
                delay(500L)
                val damage = Random.nextInt(3, 7)
                player.health -= damage
                damageMessage = "-$damage"
                damagePosition = Offset(player.x + 70, player.y - 50)
                showDamageEffect_1()
                showDamageMessage()
            }
        }
        delay(500L)
        playerTurn = true
        if (player.health <= 0) {
            gameOver = true
            message = "Defeat"
        } else if (enemies.isEmpty()) {
            gameOver = true
            message = "Victory"
        }
    }

    fun performAttack() {
        if (selectedEnemyIndex != -1 && playerTurn) {
            val damage = Random.nextInt(10, 21)
            enemies[selectedEnemyIndex].health -= damage
            damageMessage = "-$damage"
            damagePosition = Offset(enemies[selectedEnemyIndex].x + enemies[selectedEnemyIndex].width / 2 + 30, enemies[selectedEnemyIndex].y - 25)
            enemydamaged = true
            if (enemies[selectedEnemyIndex].health <= 0) {
                enemies = enemies.filterIndexed { index, _ -> index != selectedEnemyIndex }
                selectedEnemyIndex = -1
            }

            playerQuizResult = false
            playerTurn = false
        }
    }

    LaunchedEffect(Unit) {
        while(!gameOver) {
            delay(16L)
            if(!playerTurn) {
                showDamageMessage()
                performEnemyAttack()
                userDataViewModel.currentQuiz[0].createQuiz()
                playerTurn = true
                recomposeKey = !recomposeKey // 강제 recompose
            }
        }
    }

    LaunchedEffect(enemydamaged) {
        delay(500L)
        enemydamaged = false
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.White)
    ) {

        Box(modifier = Modifier
            .height(400.dp)
            .fillMaxWidth()) {

            Image(
                painter = painterResource(R.drawable.background1),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(700.dp)
            )

            Canvas(modifier = Modifier
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
                }) {
                drawIntoCanvas {

                    with(player_vec){
                        translate(left = player.x, top= player.y){
                            draw(size = Size(50.dp.toPx(), 50.dp.toPx()))
                        }
                    }
                    if(damaged){
                        with(effect1_vec){
                            val damagel1 = Random.nextInt(0,60)
                            val damagel2 = Random.nextInt(-30,10)
                            translate(left = player.x+damagel1, top= player.y+damagel2){
                                draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                            }
                        }
                    }


                    enemies.forEachIndexed { index, enemy ->

                        if (index == selectedEnemyIndex) {
                            with(arrow){
                                translate(left = enemy.x+50, top= enemy.y-90){
                                    draw(size = Size(30.dp.toPx(), 30.dp.toPx()))
                                }
                            }
                        }
                        with(enemy1_vec){
                            translate(left = enemy.x, top= enemy.y){
                                draw(size = Size(60.dp.toPx(), 60.dp.toPx()))
                            }
                        }
                        drawRect(   //테두리
                            color = Color.Black,
                            topLeft = Offset(enemy.x + 23, enemy.y + enemy.height - 78),
                            size = Size(enemy.maxHealth * 6f + 14, 10f+14)
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

                        if(index == selectedEnemyIndex && enemydamaged){
                            with(effect2_vec){
                                translate(left = enemy.x-50, top= enemy.y+20){
                                    draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                                }
                            }
                        }
                    }
                    drawRect(   //테두리
                        color = Color.Black,
                        topLeft = Offset(player.x-16, (player.y-30)-7),
                        size = Size(player.maxHealth * 1.5f + 14, 10f+14)
                    )
                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(player.x-9, (player.y-30)),
                        size = Size(player.maxHealth * 1.5f, 10f)
                    )
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(player.x-9, (player.y-30)),
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
            Button(onClick = {
                showMenuDialog = true
            },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Menu")
            }
            if (message.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(message, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if(playerQuizResult) {
                Button(onClick = { performAttack() }, enabled = playerQuizResult) {
                    Text("공격")
                }
                Button(onClick = {  }, enabled = playerQuizResult) {
                    Text("방어")
                }
                Button(onClick = {  }, enabled = playerQuizResult) {
                    Text("도망")
                }
            }
            else {
                Button(onClick = { playerTurn = false }, enabled = playerTurn) {
                    Text("다음 턴")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        WordQuiz(userDataViewModel.currentQuiz[0], recomposeKey = recomposeKey, !playerTurn, onSubmit = {quizResult ->
            playerQuizResult = quizResult
            if(playerQuizResult) playerTurn = true
        })

        if(showMenuDialog) {
            GameMenuDialog(onDismiss = { showMenuDialog = false },
                onExitGame = {
                    uiViewModel.showBottomNavigationBar.value = true
                    navController.navigate(Routes.GameListScreen.route) {
                        popUpTo(Routes.GamePlayScreen_1.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun GameMenuDialog(onDismiss: () -> Unit, onExitGame: () -> Unit) {
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