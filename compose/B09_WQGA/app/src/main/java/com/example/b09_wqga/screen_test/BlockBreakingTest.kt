package com.example.b09_wqga.screen_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.example.b09_wqga.ui.theme.B09_WQGATheme
import kotlin.random.Random

data class BlockTest(val x: Float,
                     val y: Float,
                     val width: Float,
                     val height: Float,
                     val quizBlock: Boolean,
                     var showExclamation: Boolean = false,
                     var timestamp: Long = 0L,
                     var isBroken: Boolean = false
)

data class BallTest(var x: Float, var y: Float, var radius: Float, var vx: Float, var vy: Float)

data class PaddleTest(var x: Float, val y: Float, val width: Float, val height: Float)

@Composable
fun BlockBreakingGameTest() {
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    var blocks by remember {
        mutableStateOf(emptyList<BlockTest>())
    }

    var ball by remember {
        mutableStateOf(BallTest(x = 0f, y = 0f, radius = 20f, vx = 6f, vy = 6f))
    }

    var paddle by remember {
        mutableStateOf(PaddleTest(x = 0f, y = 0f, width = 0f, height = 20f))
    }

    var lives by remember { mutableStateOf(3) }
    var gameOver by remember { mutableStateOf(false) }

    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width > 0 && canvasSize.height > 0) {
            val blockWidth = canvasSize.width / 5f
            val blockHeight = canvasSize.height / 20f

            blocks = List(30) { index ->
                BlockTest(
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

            paddle = PaddleTest(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .onSizeChanged { size ->
                canvasSize = size
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    paddle = paddle.copy(
                        x = (paddle.x + dragAmount.x).coerceIn(0f, canvasSize.width - paddle.width)
                    )
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIntoCanvas {canvas ->
                blocks.forEach { block ->

                    if(!block.isBroken) {
                        if(block.quizBlock) {
                            drawRect(
                                color = Color.Magenta,
                                topLeft = androidx.compose.ui.geometry.Offset(block.x, block.y),
                                size = androidx.compose.ui.geometry.Size(block.width, block.height)
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
                                topLeft = androidx.compose.ui.geometry.Offset(block.x, block.y),
                                size = androidx.compose.ui.geometry.Size(block.width, block.height)
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
                    center = androidx.compose.ui.geometry.Offset(ball.x, ball.y)
                )

                drawRect(
                    color = Color.Green,
                    topLeft = androidx.compose.ui.geometry.Offset(paddle.x, paddle.y),
                    size = androidx.compose.ui.geometry.Size(paddle.width, paddle.height)
                )
            }
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
}

@Preview
@Composable
fun BlockBreakingGameTestPreview() {
    B09_WQGATheme {
        BlockBreakingGameTest()
    }
}