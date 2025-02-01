package org.example

import java.io.File

const val LEARNED_THRESHOLD = 3
const val PERCENT_MULTIPLIER = 100
const val ANSWER_OPTIONS_COUNT = 4

fun loadDictionary(fileName: String): List<Word> {
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

fun calculateStatistics(dictionary: List<Word>) {
    val totalCount = dictionary.size
    val learnedCount = dictionary.count { it.correctAnswerCount >= LEARNED_THRESHOLD }

    val percent = if (totalCount > 0) (learnedCount * PERCENT_MULTIPLIER / totalCount) else 0
    println("Выучено $learnedCount из $totalCount слов | $percent%")
}

fun learnWords(dictionary: List<Word>) {
    while (true) {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < LEARNED_THRESHOLD}

        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            break
        }
        val questionWords = notLearnedList.shuffled().take(ANSWER_OPTIONS_COUNT)

        val correctAnswer = questionWords.first()

        val answerOptions = questionWords.shuffled()

        println("\n${correctAnswer.original}:")
        answerOptions.forEachIndexed { index, word ->
            println(" ${index + 1} - ${word.translate}")
        }

        print("Введите номер ответа: ")
        val userAnswer = readlnOrNull()?.toIntOrNull()
   }
}

fun main() {
    val dictionary = loadDictionary("words.txt")

    while(true) {
        println("Меню:")
        Action.entries.forEach {
            println("${it.number} - ${it.title}")
        }

        print("Введите ваш выбор: ")
        val userAnswer = readlnOrNull()?.toIntOrNull()

        when (userAnswer) {
            Action.LEARN_WORDS.number -> {
                println("Выбран пункт \"${Action.LEARN_WORDS.title}\"")
                learnWords(dictionary)
            }
            Action.STATS.number -> {
                println("Выбран пункт \"${Action.STATS.title}\"")
                calculateStatistics(dictionary)
                println()
            }
            Action.EXIT.number -> {
                println("Выход из программы")
                break
            }
            else ->  println("Введите число 1, 2 или 0")
        }
    }
}


