package org.example

import java.io.File

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val fileName: String
) {
    private var question: Question? = null
    private val dictionary = loadDictionary(fileName)

    fun getStatistics(): Statistics {
        val totalCount: Int = dictionary.size
        val learnedCount: Int = dictionary.count { it.correctAnswerCount >= LEARNED_THRESHOLD }
        val percent: Int = if (totalCount > 0) (learnedCount * PERCENT_MULTIPLIER / totalCount) else 0
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < LEARNED_THRESHOLD }
        if (notLearnedList.isEmpty()) return null

        val questionWords = if(notLearnedList.size < ANSWER_OPTIONS_COUNT) {
            val learnedList = dictionary.filter { it.correctAnswerCount >= LEARNED_THRESHOLD }.shuffled()
            notLearnedList.shuffled().take(ANSWER_OPTIONS_COUNT) +
                    learnedList.take(ANSWER_OPTIONS_COUNT - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(ANSWER_OPTIONS_COUNT)
        }.shuffled()

        val correctAnswer = questionWords.first()
        val answerOptions = questionWords.shuffled()
        question = Question(answerOptions, correctAnswer)
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(fileName: String): List<Word> {
        val wordsFile = File(fileName)
        val dictionary = mutableListOf<Word>()

        wordsFile.forEachLine {
            val parts = it.split("|")
            val correctAnswer = parts.getOrNull(2)?.toIntOrNull() ?: 0
            val word = Word(parts[0], parts[1], correctAnswer)
            dictionary.add(word)
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        File(fileName).writeText(
            dictionary.joinToString("\n") { "${it.original}|${it.translate}|${it.correctAnswerCount}" }
        )
    }
}

