package com.example.b09_wqga.screen_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import com.example.b09_wqga.ui.theme.B09_WQGATheme
import kotlinx.coroutines.delay
import kotlin.random.Random

data class EnemyTest(var x: Float, var y: Float, var width: Float, var height: Float, var health: Int, var maxHealth: Int)

data class PlayerTest(var x: Float, var y: Float, var radius: Float, var health: Int, val maxHealth: Int)

@Composable
fun RPGGameTest() {
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var player by remember { mutableStateOf(PlayerTest(x = 0f, y = 0f, radius = 30f, health = 100, maxHealth = 100)) }
    var enemies by remember { mutableStateOf(emptyList<EnemyTest>()) }
    var selectedEnemyIndex by remember { mutableStateOf(-1) }
    var message by remember { mutableStateOf("") }
    var playerTurn by remember { mutableStateOf(true) }
    var damageMessage by remember { mutableStateOf("") }
    var damagePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var gameOver by remember { mutableStateOf(false) }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            player = player.copy(
                x = canvasSize.width / 6f,
                y = canvasSize.height / 2f,
            )

            enemies = List(3) { index ->
                EnemyTest(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .onSizeChanged { size ->
                canvasSize = size
            }
    ) {
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
            }) {
            drawIntoCanvas {
                drawCircle(
                    color = Color.Green,
                    radius = player.radius,
                    center = androidx.compose.ui.geometry.Offset(player.x, player.y)
                )

                enemies.forEachIndexed { index, enemy ->
                    if (index == selectedEnemyIndex) {
                        drawRect(
                            color = Color.Red,
                            topLeft = androidx.compose.ui.geometry.Offset(enemy.x - 10, enemy.y - 10),
                            size = androidx.compose.ui.geometry.Size(enemy.width + 20, enemy.height + 20)
                        )
                    }
                    drawRect(
                        color = Color.Blue,
                        topLeft = androidx.compose.ui.geometry.Offset(enemy.x, enemy.y),
                        size = androidx.compose.ui.geometry.Size(enemy.width, enemy.height)
                    )
                    drawRect(
                        color = Color.Gray,
                        topLeft = androidx.compose.ui.geometry.Offset(enemy.x - 30, enemy.y + enemy.height + 15),
                        size = androidx.compose.ui.geometry.Size(enemy.maxHealth * 6f, 20f)
                    )
                    drawRect(
                        color = Color.Red,
                        topLeft = androidx.compose.ui.geometry.Offset(enemy.x - 30, enemy.y + enemy.height + 15),
                        size = androidx.compose.ui.geometry.Size(enemy.health * 6f, 20f)
                    )
                }

                drawRect(
                    color = Color.Gray,
                    topLeft = androidx.compose.ui.geometry.Offset(player.x - player.radius * 3, player.y + player.radius + 30),
                    size = androidx.compose.ui.geometry.Size(player.maxHealth * 2f, 20f)
                )
                drawRect(
                    color = Color.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(player.x - player.radius * 3, player.y + player.radius + 30),
                    size = androidx.compose.ui.geometry.Size(player.health * 2f, 20f)
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
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(onClick = { performAttack() }, enabled = playerTurn) {
                Text("Attack!")
            }
        }
    }
}

@Preview
@Composable
fun RPGGameTestPreview() {
    B09_WQGATheme {
        RPGGameTest()
    }
}