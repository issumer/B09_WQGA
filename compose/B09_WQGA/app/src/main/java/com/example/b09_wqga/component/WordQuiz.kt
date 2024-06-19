package com.example.b09_wqga.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.database.Quiz
import com.example.b09_wqga.ui.theme.pixelFont2

@Composable
fun WordQuiz(quiz: Quiz?, recomposeKey: Boolean, quizLoading: Boolean, onSubmit: (Boolean) -> Unit) {
    if (quizLoading) {
        LoadingQuiz()
    } else {
        quiz?.let {
            when (it.multipleOrShort) {
                0 -> {
                    when (it.multipleSelectType) {
                        0 -> {
                            MultipleChoiceQuiz1(it, onSubmit = onSubmit)
                        }
                        1 -> {
                            //MultipleChoiceQuiz2(it, onSubmit = onSubmit) // 모두 고르시오 제외
                            MultipleChoiceQuiz1(it, onSubmit = onSubmit)
                        }
                    }
                }
                1 -> {
                    ShortAnswerQuiz1(it, onSubmit = onSubmit)
                }
            }
        } ?: run {
            // quiz가 null인 경우 기본 UI 처리
            Text(text = "퀴즈를 불러오는 중 오류가 발생했습니다.", color = Color.Red)
        }
    }
}


@Composable
fun MultipleChoiceQuiz1(quiz: Quiz, onSubmit: (Boolean) -> Unit) {
    var selectedOption by remember { mutableStateOf(-1) }
    var answerChecked by remember { mutableStateOf(false) }
    val options = quiz.options

    LaunchedEffect(quiz) {
        selectedOption = -1
        answerChecked = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Q. ${quiz.question}",
            fontSize = 20.sp,
            fontFamily = pixelFont2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                RadioButton(
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Black,
                        unselectedColor = Color.Gray
                    ),
                    selected = selectedOption == index,
                    onClick = { selectedOption = index }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, fontFamily = pixelFont2,fontSize = 16.sp)
            }
        }

        if (answerChecked) {
            if (quiz.checkMultipleChoiceAnswer(listOf(selectedOption))) {
                Text(
                    text = "정답! 답: ${quiz.correctAnswer}",
                    fontFamily = pixelFont2,
                    fontSize = 16.sp,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "오답! 답: ${quiz.correctAnswer}",
                    fontFamily = pixelFont2,
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                val result = quiz.checkMultipleChoiceAnswer(listOf(selectedOption))
                Log.d("MultipleChoiceQuiz1", "Quiz result: $result, selectedOption: $selectedOption")
                onSubmit(result)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인", fontFamily = pixelFont2,)
        }
    }
}




@Composable
fun MultipleChoiceQuiz2(quiz: Quiz, onSubmit: (Boolean) -> Unit) {
    var selectedIndexes by remember { mutableStateOf(setOf<Int>()) }
    var answerChecked by remember { mutableStateOf(false) }
    var showWrongMessage by remember { mutableStateOf(false) }
    val options = quiz.options

    LaunchedEffect(quiz) {
        selectedIndexes = setOf()
        answerChecked = false
        showWrongMessage = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Q. ${quiz.question}",
            fontSize = 20.sp,
            fontFamily = pixelFont2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = index in selectedIndexes,
                    onCheckedChange = { isChecked ->
                        selectedIndexes = if (isChecked) {
                            selectedIndexes + index
                        } else {
                            selectedIndexes - index
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, fontFamily = pixelFont2, fontSize = 16.sp)
            }
        }

        when (quiz.currentAnswerCorrect) {
            1 -> Text(
                text = "정답! 답: ${quiz.correctAnswer}",
                fontSize = 16.sp,
                fontFamily = pixelFont2,
                color = Color.Blue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            2 -> Text(
                text = "오답! 답: ${quiz.correctAnswer}",
                fontSize = 16.sp,
                fontFamily = pixelFont2,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            else -> if (showWrongMessage) {
                Text(
                    text = "틀렸습니다",
                    fontSize = 16.sp,
                    fontFamily = pixelFont2,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                val result = quiz.checkMultipleChoiceAnswer(selectedIndexes.toMutableList())
                Log.d("MultipleChoiceQuiz2", "Quiz result: $result, selectedIndexes: $selectedIndexes")
                onSubmit(result)
                if (!result) {
                    showWrongMessage = true
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인",fontFamily = pixelFont2,)
        }
    }
}









@Composable
fun ShortAnswerQuiz1(quiz: Quiz, onSubmit: (Boolean) -> Unit) {
    var userAnswer by remember { mutableStateOf("") }
    var answerChecked by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Q. ${quiz.question}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)

        OutlinedTextField(value = userAnswer, onValueChange = { userAnswer = it }, label = { Text("답", fontFamily = pixelFont2) })

        when (quiz.currentAnswerCorrect) {

            1 -> Text(text = "정답! 답: ${quiz.correctAnswer}", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 16.dp))
            2 -> Text(text = "오답! 답: ${quiz.correctAnswer}", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))

            else -> Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                onSubmit(quiz.checkShortAnswerAnswer(userAnswer))
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인",fontFamily = pixelFont2)
        }
    }
}

@Composable
fun MultipleChoiceQuiz1(quiz: Quiz, selectedOption: Int = -1, answerChecked: Boolean = false, onSubmit: (Boolean) -> Unit) {
    var selectedOption by remember { mutableStateOf(selectedOption) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val options = quiz.choiceOptionsWordData.orEmpty() // Null 방지
    val scrollState = rememberScrollState()

    LaunchedEffect(null) {
        selectedOption = -1
        answerChecked = false
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.question}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp),fontFamily = pixelFont2)

        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                RadioButton(
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Black,
                        unselectedColor = Color.Gray
                    ),
                    selected = selectedOption == index,
                    onClick = { selectedOption = index }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option.headword, fontSize = 16.sp, fontFamily = pixelFont2)
            }
        }

        val answerNums = quiz.answerNums.orEmpty().map { (it + 1).toString() } // Null 방지
        val answerString = answerNums.joinToString(", ")
        when (quiz.currentAnswerCorrect) {
            1 -> Text(text = "정답! 답: $answerString", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            2 -> Text(text = "오답! 답: $answerString", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            else -> Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                onSubmit(quiz.checkMultipleChoiceAnswer(mutableListOf(selectedOption)))
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인",fontFamily = pixelFont2)
        }
    }
}

@Composable
fun MultipleChoiceQuiz2(quiz: Quiz, selectedIndexes: Set<Int> = setOf(), answerChecked: Boolean = false, onSubmit: (Boolean) -> Unit) {
    var selectedIndexes by remember { mutableStateOf(selectedIndexes) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val options = quiz.options.orEmpty() // Null 방지
    val scrollState = rememberScrollState()

    LaunchedEffect(null) {
        selectedIndexes = setOf()
        answerChecked = false
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.question}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)

        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Checkbox(
                    checked = index in selectedIndexes,
                    onCheckedChange = { isChecked ->
                        selectedIndexes = if (isChecked) {
                            selectedIndexes + index
                        } else {
                            selectedIndexes - index
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, fontSize = 16.sp,fontFamily = pixelFont2)
            }
        }

        val answerNums = quiz.answerNums.orEmpty().map { (it + 1).toString() } // Null 방지
        val answerString = answerNums.joinToString(", ")
        when (quiz.currentAnswerCorrect) {
            1 -> Text(text = "정답! 답: $answerString", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            2 -> Text(text = "오답! 답: $answerString", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            else -> Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                onSubmit(quiz.checkMultipleChoiceAnswer(selectedIndexes.toMutableList()))
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인",fontFamily = pixelFont2)
        }
    }
}

@Composable
fun ShortAnswerQuiz1(quiz: Quiz, userAnswer: String = "", answerChecked: Boolean = false, onSubmit: (Boolean) -> Unit) {
    var userAnswer by remember { mutableStateOf(userAnswer) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.question}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)

        OutlinedTextField(value = userAnswer, onValueChange = { userAnswer = it }, label = { Text("답",fontFamily = pixelFont2) })

        val answerWord = quiz.answerWord ?: return // Null 방지
        when (quiz.currentAnswerCorrect) {
            1 -> Text(text = "정답! 단어: ${answerWord.headword}", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            2 -> Text(text = "오답! 단어: ${answerWord.headword}", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
            else -> Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp),fontFamily = pixelFont2)
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White),
            onClick = {
                answerChecked = true
                onSubmit(quiz.checkShortAnswerAnswer(userAnswer))
            },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(12.dp),
            enabled = !answerChecked
        ) {
            Text("정답 확인",fontFamily = pixelFont2)
        }
    }
}

@Composable
fun LoadingQuiz() {
    val scrollState = rememberScrollState()

    Column(verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(scrollState)
    ) {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
            ){
            Text("퀴즈 로딩 중 . . .", fontFamily = pixelFont2)
        }
    }
}
