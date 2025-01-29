package org.example

import java.io.File

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
            Action.LEARN_WORDS.number -> println("Выбран пункт \"${Action.LEARN_WORDS.title}\"")
            Action.STATS.number -> println("Выбран пункт \"${Action.STATS.title}\"")
            Action.EXIT.number -> {
                println("Выход из программы")
                break
            }
            else ->  println("Введите число 1, 2 или 0")
        }
    }
}


