package com.example.b09_wqga.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.R
import com.example.b09_wqga.model.Quiz

@Composable
fun WordQuiz(quiz : Quiz, recomposeKey : Boolean, quizLoading : Boolean, onSubmit : (Boolean) -> Unit) {
    if(quizLoading) {
        LoadingQuiz()
    } else {
        when(quiz.currentQuiz) {
            0 -> {
                when(quiz.currentQuizType) {
                    0 -> {
                        MultipleChoiceQuiz1(quiz, onSubmit = onSubmit)
                    }
                    1 -> {
                        MultipleChoiceQuiz2(quiz, onSubmit = onSubmit)
                    }
                }
            }
            1 -> {
                ShortAnswerQuiz1(quiz, onSubmit = onSubmit)
            }
        }
    }
}


@Composable
fun MultipleChoiceQuiz1(quiz : Quiz, selectedOption : Int = -1, answerChecked : Boolean = false, onSubmit : (Boolean) -> Unit) {
    var selectedOption by remember { mutableStateOf(selectedOption) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val options = quiz.choiceOptionsWordData!!
    val scrollState = rememberScrollState()

    LaunchedEffect(null) {
        selectedOption = -1
        answerChecked = false
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.description}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { selectedOption = index }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option.headword, fontSize = 16.sp)
            }
        }

        //Spacer(modifier = Modifier.height(8.dp))

        val answerNums = quiz.answerNums!!.map { (it + 1).toString() }
        val answerString = answerNums.joinToString(", ")
        if(quiz.currentAnswerCorrect == 1) {
            Text(text = "정답! 답: $answerString", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 16.dp))
        } else if(quiz.currentAnswerCorrect == 2) {
            Text(text = "오답! 답: $answerString", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        } else {
            Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(onClick = {
            answerChecked = true
            onSubmit(quiz.checkMultipleChoiceAnswer(mutableListOf<Int>(selectedOption)))

        },
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인")
        }
    }
}

@Composable
fun MultipleChoiceQuiz2(quiz : Quiz, selectedIndexes : Set<Int> = setOf<Int>(), answerChecked : Boolean = false, onSubmit : (Boolean) -> Unit) {
    var selectedIndexes by remember { mutableStateOf(selectedIndexes) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val options = quiz.choiceOptions!!
    val scrollState = rememberScrollState()

    LaunchedEffect(null) {
        selectedIndexes = setOf<Int>()
        answerChecked = false
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.description}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        options.forEachIndexed { index, option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
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
                Text(text = option, fontSize = 16.sp)
            }
        }

        //Spacer(modifier = Modifier.height(8.dp))

        val answerNums = quiz.answerNums!!.map { (it + 1).toString() }
        val answerString = answerNums.joinToString(", ")
        if(quiz.currentAnswerCorrect == 1) {
            Text(text = "정답! 답: $answerString", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 16.dp))
        } else if(quiz.currentAnswerCorrect == 2) {
            Text(text = "오답! 답: $answerString", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        } else {
            Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(onClick = {
            answerChecked = true
            onSubmit(quiz.checkMultipleChoiceAnswer(selectedIndexes.toMutableList()))

        },
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인")
        }
    }
}

@Composable
fun ShortAnswerQuiz1(quiz : Quiz, userAnswer : String = "", answerChecked : Boolean = false, onSubmit : (Boolean) -> Unit) {
    var userAnswer by remember { mutableStateOf(userAnswer) }
    var answerChecked by remember { mutableStateOf(answerChecked) }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text(text = "${quiz.currentQuizNum}. ${quiz.description}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = userAnswer,
            onValueChange = {userAnswer = it},
            label = {Text("답")}
        )

        //Spacer(modifier = Modifier.height(8.dp))

        val answerWord = quiz.answerWord!!
        if(quiz.currentAnswerCorrect == 1) {
            Text(text = "정답! 단어: ${answerWord.headword}", fontSize = 16.sp, color = Color.Blue, modifier = Modifier.padding(bottom = 16.dp))
        } else if(quiz.currentAnswerCorrect == 2) {
            Text(text = "오답! 단어: ${answerWord.headword}", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
        } else {
            Text(text = "", fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(onClick = {
            answerChecked = true
            onSubmit(quiz.checkShortAnswerAnswer(userAnswer))
        },
            modifier = Modifier.align(Alignment.End),
            enabled = !answerChecked
        ) {
            Text("정답 확인")
        }
    }
}

@Composable
fun LoadingQuiz() {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        Text("퀴즈 로딩 중")
    }
}