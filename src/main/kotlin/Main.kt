package org.example

import java.io.File

fun main() {
    val wordsFile = File("words.txt")

    val wordsFileLines = wordsFile.readLines()
    wordsFileLines.forEach {
        println(it)
    }
}