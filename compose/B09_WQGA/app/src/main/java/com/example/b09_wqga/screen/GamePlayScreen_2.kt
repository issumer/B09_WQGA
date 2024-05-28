/*
구현 목록에서 게임 2 (턴제 RPG 게임) 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.b09_wqga.navigation.Routes
import kotlinx.coroutines.delay
import kotlin.random.Random

// BB: Block Breaking
data class BBBlock(val x: Float,
                     val y: Float,
                     val width: Float,
                     val height: Float,
                     val quizBlock: Boolean,
                     var showExclamation: Boolean = false,
                     var timestamp: Long = 0L,
                     var isBroken: Boolean = false
)

data class BBBall(var x: Float, var y: Float, var radius: Float, var vx: Float, var vy: Float)

data class BBPaddle(var x: Float, val y: Float, val width: Float, val height: Float)

@Composable
fun GamePlayScreen_2(navController: NavHostController) {
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var blocks by remember {
        mutableStateOf(emptyList<BBBlock>())
    }

    var ball by remember {
        mutableStateOf(BBBall(x = 0f, y = 0f, radius = 20f, vx = 6f, vy = 6f))
    }

    var paddle by remember {
        mutableStateOf(BBPaddle(x = 0f, y = 0f, width = 0f, height = 20f))
    }
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    var lives by remember { mutableStateOf(3) } // 목숨 개수
    var gameOver by remember { mutableStateOf(false) } // 게임 종료 여부
    var gamePaused by remember { mutableStateOf(false) } // 게임 잠깐 멈춤 여부 (메뉴 열기 등의 이유로)

    var showMenuDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val blockWidth = canvasSize.width / 5f
            val blockHeight = canvasSize.height / 20f

            blocks = List(30) { index ->
                BBBlock(
                    x = (index % 5) * blockWidth,
                    y = (index / 5) * blockHeight,
                    width = blockWidth,
                    height = blockHeight,
                    quizBlock = Random.nextBoolean()
                )
            }

            ball = ball.copy(
                x = canvasSize.width / 2f,
                y = canvasSize.height / 2f,
            )

            paddle = BBPaddle(
                x = canvasSize.width / 2f - 50f,
                y = canvasSize.height - 30f,
                width = 100f,
                height = 20f
            )
        }
    }

    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(16L)
            currentTime.value = System.currentTimeMillis()

            ball = ball.copy(
                x = ball.x + ball.vx,
                y = ball.y + ball.vy
            )

            if (ball.x < ball.radius || ball.x > canvasSize.width - ball.radius) {
                ball = ball.copy(vx = -ball.vx)
            }
            if (ball.y < ball.radius) {
                ball = ball.copy(vy = -ball.vy)
            }
            if (ball.y > canvasSize.height - ball.radius) {
                lives -= 1
                if (lives > 0) {
                    ball = ball.copy(
                        x = canvasSize.width / 2f,
                        y = canvasSize.height / 2f,
                        vy = -ball.vy
                    )
                } else {
                    gameOver = true
                }
            }

            // Paddle collision detection
            val ballRect = Rect(
                ball.x - ball.radius,
                ball.y - ball.radius,
                ball.x + ball.radius,
                ball.y + ball.radius
            )
            val paddleRect = Rect(
                paddle.x,
                paddle.y,
                paddle.x + paddle.width,
                paddle.y + paddle.height
            )
            if (ballRect.overlaps(paddleRect)) {
                ball = ball.copy(vy = -ball.vy)
            }

            blocks = blocks.map { block ->
                val blockRect = Rect(
                    block.x,
                    block.y,
                    block.x + block.width,
                    block.y + block.height
                )
                if (ballRect.overlaps(blockRect) && !block.isBroken) {
                    ball = ball.copy(vy = -ball.vy)
                    if(block.quizBlock) block.copy(showExclamation = true, timestamp = currentTime.value, isBroken = true)
                    else block.copy(isBroken = true)
                } else {
                    block
                }
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
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        paddle = paddle.copy(
                            x = (paddle.x + dragAmount.x).coerceIn(
                                0f,
                                canvasSize.width - paddle.width
                            )
                        )
                    }
                }
                .onSizeChanged { size ->
                    canvasSize = size
                }) {
                drawIntoCanvas {canvas ->
                    blocks.forEach { block ->

                        if(!block.isBroken) {
                            if(block.quizBlock) {
                                drawRect(
                                    color = Color.Magenta,
                                    topLeft = Offset(block.x, block.y),
                                    size = Size(block.width, block.height)
                                )

                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    textSize = 40f
                                }
                                canvas.nativeCanvas.drawText("Quiz", block.x + block.width / 2, block.y + block.height / 2 + 15, textPaint)
                            } else {
                                drawRect(
                                    color = Color.Blue,
                                    topLeft = Offset(block.x, block.y),
                                    size = Size(block.width, block.height)
                                )
                            }
                        }
                        if (block.showExclamation && currentTime.value - block.timestamp < 2000L) {
                            val textPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 40f
                            }
                            canvas.nativeCanvas.drawText("!", block.x + block.width / 2, block.y + block.height / 2 + 15, textPaint)
                        }
                    }

                    drawCircle(
                        color = Color.Red,
                        radius = ball.radius,
                        center = Offset(ball.x, ball.y)
                    )

                    drawRect(
                        color = Color.Green,
                        topLeft = Offset(paddle.x, paddle.y),
                        size = Size(paddle.width, paddle.height)
                    )
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
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                repeat(lives) {
                    Text("❤️", color = Color.Black, modifier = Modifier.padding(end = 8.dp))
                }
            }
            if (gameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Defeat", color = Color.Black, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        WordQuiz(onSubmitClick = { })

        if(showMenuDialog) {
            GameMenuDialog(onDismiss = { showMenuDialog = false },
                onExitGame = {
                    navController.navigate(Routes.GameListScreen.route) {
                        popUpTo(Routes.GamePlayScreen_2.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

