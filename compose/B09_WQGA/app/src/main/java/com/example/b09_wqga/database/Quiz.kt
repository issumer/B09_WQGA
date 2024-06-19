package com.example.b09_wqga.database

data class Quiz(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val currentQuiz: Int = 0,
    val currentQuizType: Int = 0,
    val currentQuizNum: Int = 0,
    val choiceOptionsWordData: List<Word>? = null,
    val answerNums: List<Int>? = null,
    var currentAnswerCorrect: Int = 0,
    val answerWord: Word? = null
) {
    fun checkMultipleChoiceAnswer(selectedOptions: List<Int>): Boolean {
        val correctAnswerIndex = options.indexOf(correctAnswer)
        val isCorrect = selectedOptions.size == 1 && selectedOptions[0] == correctAnswerIndex
        currentAnswerCorrect = if (isCorrect) 1 else 2
        return isCorrect
    }

    fun checkShortAnswerAnswer(userAnswer: String): Boolean {
        val isCorrect = userAnswer.equals(answerWord?.headword, ignoreCase = true)
        currentAnswerCorrect = if (isCorrect) 1 else 2
        return isCorrect
    }
}

