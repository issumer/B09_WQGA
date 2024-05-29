/*
구현 목록에서 게임 1 (턴제 RPG 게임) 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes
import kotlinx.coroutines.delay
import kotlin.random.Random

data class RPGEnemy(var x: Float, var y: Float, var width: Float, var height: Float, var health: Int, var maxHealth: Int)

data class RPGPlayer(var x: Float, var y: Float, var radius: Float, var health: Int, val maxHealth: Int)

@Composable
fun GamePlayScreen_1(navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var player by remember { mutableStateOf(RPGPlayer(x = 0f, y = 0f, radius = 30f, health = 100, maxHealth = 100)) }
    var enemies by remember { mutableStateOf(emptyList<RPGEnemy>()) }
    var selectedEnemyIndex by remember { mutableStateOf(-1) }
    var message by remember { mutableStateOf("") }
    var playerTurn by remember { mutableStateOf(true) }
    var damageMessage by remember { mutableStateOf("") }
    var damagePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var gameOver by remember { mutableStateOf(false) } // 게임 종료 여부
    var gamePaused by remember { mutableStateOf(false) } // 게임 잠깐 멈춤 여부 (메뉴 열기 등의 이유로)
    var showMenuDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            player = player.copy(
                x = canvasSize.width / 6f,
                y = canvasSize.height / 2f,
            )

            enemies = List(3) { index ->
                RPGEnemy(
                    x = canvasSize.width * 3 / 4f,
                    y = canvasSize.height * (index + 1) / 4f,
                    width = 60f,
                    height = 60f,
                    health = 20,
                    maxHealth = 20
                )
            }
        }
    }

    suspend fun showDamageMessage() {
        delay(1000L)
        damageMessage = ""
    }

    suspend fun performEnemyAttack() {
        if (enemies.isNotEmpty()) {
            enemies.forEach { enemy ->
                delay(500L)
                val damage = Random.nextInt(3, 7)
                player.health -= damage
                damageMessage = "-$damage"
                damagePosition = Offset(player.x, player.y - 50)
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
            damagePosition = Offset(enemies[selectedEnemyIndex].x + enemies[selectedEnemyIndex].width / 2, enemies[selectedEnemyIndex].y - 25)

            if (enemies[selectedEnemyIndex].health <= 0) {
                enemies = enemies.filterIndexed { index, _ -> index != selectedEnemyIndex }
                selectedEnemyIndex = -1
            }

            playerTurn = false
        }
    }

    LaunchedEffect(Unit) {
        while(!gameOver) {
            delay(16L)
            if(!playerTurn) {
                showDamageMessage()
                performEnemyAttack()
                playerTurn = true
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.White)
        ) {
        Box(modifier = Modifier
            .height(440.dp)
            .fillMaxWidth()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val clickedEnemyIndex = enemies.indexOfFirst { enemy ->
                            offset.x >= enemy.x && offset.x <= enemy.x + enemy.width &&
                                    offset.y >= enemy.y && offset.y <= enemy.y + enemy.height
                        }
                        if (clickedEnemyIndex != -1 && playerTurn) {
                            selectedEnemyIndex = clickedEnemyIndex
                        }
                    }
                }.onSizeChanged { size ->
                    canvasSize = size
                }) {
                drawIntoCanvas {
                    drawCircle(
                        color = Color.Green,
                        radius = player.radius,
                        center = Offset(player.x, player.y)
                    )

                    enemies.forEachIndexed { index, enemy ->
                        if (index == selectedEnemyIndex) {
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(enemy.x - 10, enemy.y - 10),
                                size = Size(enemy.width + 20, enemy.height + 20)
                            )
                        }
                        drawRect(
                            color = Color.Blue,
                            topLeft = Offset(enemy.x, enemy.y),
                            size = Size(enemy.width, enemy.height)
                        )
                        drawRect(
                            color = Color.Gray,
                            topLeft = Offset(enemy.x - 30, enemy.y + enemy.height + 15),
                            size = Size(enemy.maxHealth * 6f, 20f)
                        )
                        drawRect(
                            color = Color.Red,
                            topLeft = Offset(enemy.x - 30, enemy.y + enemy.height + 15),
                            size = Size(enemy.health * 6f, 20f)
                        )
                    }

                    drawRect(
                        color = Color.Gray,
                        topLeft = Offset(player.x - player.radius * 3, player.y + player.radius + 30),
                        size = Size(player.maxHealth * 2f, 20f)
                    )
                    drawRect(
                        color = Color.Red,
                        topLeft = Offset(player.x - player.radius * 3, player.y + player.radius + 30),
                        size = Size(player.health * 2f, 20f)
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
            Button(onClick = { performAttack() }, enabled = playerTurn) {
                Text("Attack")
            }
            Button(onClick = {  }, enabled = playerTurn) {
                Text("Defend")
            }
            Button(onClick = {  }, enabled = playerTurn) {
                Text("Run")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        WordQuiz(onSubmitClick = { })

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
fun WordQuiz(onSubmitClick: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("Selection 1", "Selection 2", "Selection 3", "Selection 4", "Selection 5")
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "Quiz Question", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Answer or Wrong", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))

        Button(onClick = onSubmitClick, modifier = Modifier.align(Alignment.End)) {
            Text("Submit")
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