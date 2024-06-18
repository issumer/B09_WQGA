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
    val currentAnswerCorrect: Int = 0,
    val answerWord: Word? = null
) {
    fun checkMultipleChoiceAnswer(selectedOptions: MutableList<Int>): Boolean {
        return selectedOptions.isNotEmpty() && selectedOptions[0] == answerNums?.get(0)
    }

    fun checkShortAnswerAnswer(userAnswer: String): Boolean {
        return userAnswer.equals(answerWord?.headword, ignoreCase = true)
    }
}
