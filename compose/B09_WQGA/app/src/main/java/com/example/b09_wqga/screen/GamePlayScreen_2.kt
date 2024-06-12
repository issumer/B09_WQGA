/*
구현 목록에서 게임 2 (턴제 RPG 게임) 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.WordQuiz
import com.example.b09_wqga.model.UIViewModel
import com.example.b09_wqga.model.UserDataViewModel
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
                   var isBroken: Int = 2, // 0 부서진 상태, 1 부분 깨진 상태, 2 멀쩡한 상태
                   var colorCode: Int = (1..2).random()
)

data class BBBall(var x: Float, var y: Float, var radius: Float, var vx: Float, var vy: Float)

data class BBPaddle(var x: Float, val y: Float, val width: Float, val height: Float)

@Composable
fun GamePlayScreen_2(navController: NavHostController) {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val uiViewModel: UIViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
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
    var score by remember { mutableStateOf(0) }
    var rightCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }

    var showMenuDialog by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }

    val ball_vec : Painter = painterResource(id = R.drawable.ball)
    val paddle1 : Painter = painterResource(id = R.drawable.paddle1)
    val bluebrick : Painter = painterResource(id = R.drawable.bluebrick)
    val redbrick : Painter = painterResource(id = R.drawable.redbrick)
    val yellowbrick : Painter = painterResource(id = R.drawable.yellowbrick)
    val bluebrickc : Painter = painterResource(id = R.drawable.bluebrickc)
    val redbrickc : Painter = painterResource(id = R.drawable.redbrickc)


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
                width = 150f,
                height = 30f
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
                if (ballRect.overlaps(blockRect) && block.isBroken != 0) {
                    ball = ball.copy(vy = -ball.vy)

                    if(block.isBroken == 2) {
                        if(block.quizBlock) block.copy(showExclamation = true, timestamp = currentTime.value, isBroken = 0)
                        else block.copy(isBroken = 1)
                    }
                    else {
                        if(block.quizBlock) block.copy(showExclamation = true, timestamp = currentTime.value, isBroken = 0)
                        else block.copy(isBroken = 0)
                    }
                    
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

            Image(painter = painterResource(id = R.drawable.background2),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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

                        if(block.isBroken != 0) {
                            if(block.quizBlock) {

                                with(yellowbrick){
                                    translate(left = block.x, top= block.y){
                                        draw(size = Size(block.width, block.height))
                                    }
                                }

                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLACK
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    textSize = 40f
                                }
                                canvas.nativeCanvas.drawText("Quiz", block.x + block.width / 2, block.y + block.height / 2 + 15, textPaint)
                            } else { // 퀴즈 블록이 아닐 경우
                                if(block.isBroken == 2){
                                    if(block.colorCode == 1){   // red
                                        with(redbrick){
                                            translate(left = block.x, top= block.y){
                                                draw(size = Size(block.width, block.height))
                                            }
                                        }
                                    }

                                    else {   // blue
                                        with(bluebrick){
                                            translate(left = block.x, top= block.y){
                                                draw(size = Size(block.width, block.height))
                                            }
                                        }
                                    }
                                }
                                else{
                                    if(block.colorCode == 1){   // red
                                        with(redbrickc){
                                            translate(left = block.x, top= block.y){
                                                draw(size = Size(block.width, block.height))
                                            }
                                        }
                                    }

                                    else {   // blue
                                        with(bluebrickc){
                                            translate(left = block.x, top= block.y){
                                                draw(size = Size(block.width, block.height))
                                            }
                                        }
                                    }
                                }

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

                    with(ball_vec){
                        translate(left = ball.x, top= ball.y){
                            draw(size = Size(30f, 30f))
                        }
                    }

                    with(paddle1){
                        translate(left = paddle.x, top= paddle.y){
                            draw(size = Size(paddle.width, paddle.height))
                        }
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

        //WordQuiz(onSubmitClick = { })

        if(showMenuDialog) {
            GameMenuDialog(onDismiss = { showMenuDialog = false },
                onExitGame = {
                    uiViewModel.showBottomNavigationBar.value = true
                    navController.navigate(Routes.GameListScreen.route) {
                        popUpTo(Routes.GamePlayScreen_2.route) {
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

