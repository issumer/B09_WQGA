/*
구현 목록에서 게임 2 (벽돌깨기) 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.WordQuiz
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    /* GamePlayScreen_1에서 데이터 가져오는 부분 해결된 후 수정할 예정이라
    잠깐 주석처리해놓을게요 (주석처리 해제하면 오류나요)
     val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
     val gameDifficulty = userDataViewModel.gameDifficulty.value
     var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

     var blocks by remember {
         mutableStateOf(emptyList<BBBlock>())
     }

     var ball by remember {
         mutableStateOf(BBBall(x = 0f, y = 0f, radius = 20f,
             vx = when(gameDifficulty) {
                 0 -> 6f
                 1 -> 8f
                 2 -> 10f
                 else -> 6f
             },
             vy = when(gameDifficulty) {
                 0 -> 6f
                 1 -> 8f
                 2 -> 10f
                 else -> 6f
             })
         )
     }

     var paddle by remember {
         mutableStateOf(BBPaddle(x = 0f, y = 0f, width = 0f, height = 20f))
     }
     val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

     var lives by remember { mutableStateOf(3) } // 목숨 개수
     var gameOver by remember { mutableStateOf(false) } // 게임 종료 여부
     var gameWin by remember { mutableStateOf(false) } // 게임 승리 여부
     var playerQuizResult by remember { mutableStateOf(false) }
     var playerQuizPaused by remember { mutableStateOf(false) }
     var score by remember { mutableStateOf(0) }
     var rightCount by remember { mutableStateOf(0) }
     var wrongCount by remember { mutableStateOf(0) }
     var showWrong: Boolean by remember { mutableStateOf(false)}
     var showCorrect: Boolean by remember { mutableStateOf(false)}
     var showLiveDecrease: Boolean by remember { mutableStateOf(false)}


     var showMenuDialog by rememberSaveable {
         mutableStateOf<Boolean>(false)
     }

     var showWordQuiz by remember { mutableStateOf(false) } // 퀴즈 표시 여부

     var recomposeKey by remember { mutableStateOf(false) }

     var coroutineScope = rememberCoroutineScope()


     val ball_vec : Painter = painterResource(id = R.drawable.ball)
     val paddle1 : Painter = painterResource(id = R.drawable.paddle1)
     val bluebrick : Painter = painterResource(id = R.drawable.bluebrick)
     val redbrick : Painter = painterResource(id = R.drawable.redbrick)
     val yellowbrick : Painter = painterResource(id = R.drawable.yellowbrick)
     val bluebrickc : Painter = painterResource(id = R.drawable.bluebrickc)
     val redbrickc : Painter = painterResource(id = R.drawable.redbrickc)
     val lifedecrease : Painter = painterResource(id = R.drawable.lifedecrease)
     val wrong : Painter = painterResource(id = R.drawable.wrong)
     val correct : Painter = painterResource(id = R.drawable.correct)
     val heart3 : Painter = painterResource(id = R.drawable.heart3)
     val heart2 : Painter = painterResource(id = R.drawable.heart2)
     val heart1 : Painter = painterResource(id = R.drawable.heart1)
     val heart0 : Painter = painterResource(id = R.drawable.heart0)


     fun nextQuiz() {
         if(rightCount > 0 || wrongCount > 0) { // 첫번째 퀴즈는 새로운 퀴즈를 만들지 않음
             userDataViewModel.currentQuiz[0].createQuiz()
         }
         playerQuizPaused = false
         showWordQuiz = true
         recomposeKey = !recomposeKey // 강제 recompose
     }


     suspend fun quizFinished() {
         delay(2000L)
         playerQuizPaused = true
         showWordQuiz = false
     }


     LaunchedEffect(canvasSize) {
         if (canvasSize.width > 0 && canvasSize.height > 0) {
             val blockWidth = canvasSize.width / 5f
             val blockHeight = canvasSize.height / 20f

             val quizBlockCount = when (gameDifficulty) {
                 0 -> 6
                 1 -> 13
                 2 -> 20
                 else -> 10
             }

             blocks = List(when(gameDifficulty) {
                 0 -> 30
                 1 -> 35
                 2 -> 40
                 else -> 30
             }) { index ->
                 BBBlock(
                     x = (index % 5) * blockWidth,
                     y = (index / 5) * blockHeight,
                     width = blockWidth,
                     height = blockHeight,
                     quizBlock = false
                 )
             }
             blocks = blocks.shuffled().mapIndexed { index, block ->
                 if (index < quizBlockCount) {
                     block.copy(quizBlock = true)
                 } else {
                     block
                 }
             }

 //            blocks = List(40) { index ->
 //                BBBlock(
 //                    x = (index % 5) * blockWidth,
 //                    y = (index / 5) * blockHeight,
 //                    width = blockWidth,
 //                    height = blockHeight,
 //                    quizBlock = Random.nextBoolean()
 //                )
 //            }

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

     LaunchedEffect(lives){
         if(lives != 3) {
             showLiveDecrease = true
             delay(1000)
             showLiveDecrease = false
         }
     }

     LaunchedEffect(showCorrect){
         delay(1000)
         showCorrect = false
     }

     LaunchedEffect(showWrong) {
         delay(1000)
         showWrong = false
     }

     LaunchedEffect(Unit) {
         while (!gameOver) {
             if (!showWordQuiz && !showMenuDialog) {
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
                     showLiveDecrease = true
                     delay(1000)
                     showLiveDecrease = false
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

                         if (block.isBroken == 2) {
                             if (block.quizBlock) {
                                 block.copy(
                                     showExclamation = true,
                                     timestamp = currentTime.value,
                                     isBroken = 0
                                 )
                                     .also {
                                         nextQuiz()
                                     }// 퀴즈 표시
                             } else block.copy(isBroken = 1)
                         } else {
                             if (block.quizBlock) {
                                 block.copy(
                                     showExclamation = true,
                                     timestamp = currentTime.value,
                                     isBroken = 0
                                 )
                                     .also {
                                         nextQuiz()
                                     } // 퀴즈 표시
                             } else block.copy(isBroken = 0)
                         }

                     } else {
                         block
                     }
                 }
                 // 모든 벽돌이 깨졌는지 확인
                 if (blocks.all { it.isBroken == 0 }) {
                     gameWin = true
                     gameOver = true
                 }
             } else {
                 // 퀴즈가 활성화되면 게임 루프가 일시 중지됨
                 delay(30L)
             }
         }
     }
     if (gameOver) {
         if (lives == 0) {
             gameWin = false
         } else if (blocks.all { it.isBroken == 0 }) {
             gameWin = true
         }
     }

     Column(modifier = Modifier
         .fillMaxSize()
         .padding(16.dp)
         .background(Color.White)
     ) {
         Box(modifier = Modifier
             .height(70.dp)
             .fillMaxWidth()
             .background(Color.Black)
         ) {
             Canvas(
                 modifier = Modifier
                     .fillMaxSize()
             ) {
                 if (lives == 3) {
                     with(heart3) {
                         translate(left = 30f, top = 20f) {
                             draw(size = Size(80.dp.toPx(), 26.dp.toPx()))
                         }
                     }
                 } else if (lives == 2) {
                     with(heart2) {
                         translate(left = 30f, top = 20f) {
                             draw(size = Size(80.dp.toPx(), 26.dp.toPx()))
                         }
                     }
                 } else if (lives == 1) {
                     with(heart1) {
                         translate(left = 30f, top = 20f) {
                             draw(size = Size(80.dp.toPx(), 26.dp.toPx()))
                         }
                     }
                 } else {
                     with(heart0) {
                         translate(left = 30f, top = 20f) {
                             draw(size = Size(80.dp.toPx(), 26.dp.toPx()))
                         }
                     }
                 }
             }
             Row(horizontalArrangement = Arrangement.End,
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(end = 5.dp, top = 10.dp)){
                 Image(painter = painterResource(id = R.drawable.pausebutton_2),
                     contentDescription = "menu",
                     contentScale = ContentScale.Crop,
                     modifier = Modifier
                         .clickable { showMenuDialog = true }
                         .size(width = 70.dp, height = 30.dp)
                 )
             }
         }
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
                     if(showLiveDecrease){
                         with(lifedecrease){
                             translate(left = 360f, top= 500f){
                                 draw(size = Size(100.dp.toPx(), 45.dp.toPx()))
                             }
                         }
                     }

                     if(showWrong){
                         with(wrong){
                             translate(left = 420f, top= 650f){
                                 draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                             }
                         }
                     }

                     if(showCorrect){
                         with(correct){
                             translate(left = 420f, top= 500f){
                                 draw(size = Size(70.dp.toPx(), 70.dp.toPx()))
                             }
                         }
                     }
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
         }

         Spacer(modifier = Modifier.height(16.dp))


         if (showWordQuiz) {
             WordQuiz(userDataViewModel.currentQuiz[0], recomposeKey = recomposeKey, playerQuizPaused, onSubmit = {quizResult ->
                 playerQuizResult = quizResult
                 if(playerQuizResult) {
                     showCorrect = true
                     rightCount += 1
                     coroutineScope.launch {
                         quizFinished()
                     }
                 } else {
                     showWrong = true
                     wrongCount += 1
                     lives -= 1 //목숨 하나 까기
                     if(lives == 0) {
                         gameOver = true
                     } else {
                         coroutineScope.launch {
                             quizFinished()
                         }
                     }
                 }
             })
         }

         if(showMenuDialog) {
             GameMenuDialog(onDismiss = {
                 if(!gameOver) {
                     showMenuDialog = false
                 }},
                 onExitGame = {
                     userDataViewModel.showBottomNavigationBar.value = true
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

     */
}
