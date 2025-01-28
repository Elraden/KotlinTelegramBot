package org.example

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()

    wordsFile.forEachLine {
        val parts = it.split("|")
        val correctAnswer = parts.getOrNull(2)?.toIntOrNull() ?: 0
        val word = Word(parts[0], parts[1], correctAnswer)
        dictionary.add(word)
    }

    println(dictionary)
}