package org.example

const val LEARNED_THRESHOLD = 3
const val PERCENT_MULTIPLIER = 100
const val ANSWER_OPTIONS_COUNT = 4


fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> " ${index + 1} - ${word.translate}" }
        .joinToString("\n")
    return "${this.correctAnswer.original}\n${variants}\n ----------\n 0 - Меню"
}

fun main() {
    val fileName = "words.txt"
    val trainer = LearnWordsTrainer(fileName)

    while(true) {
        println("Меню:")
        Action.entries.forEach {
            println("${it.number} - ${it.title}")
        }

        print("Введите ваш выбор: ")
        when (readlnOrNull()?.toIntOrNull()) {
            Action.LEARN_WORDS.number -> {
                println("Выбран пункт \"${Action.LEARN_WORDS.title}\"")
                while(true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова в словаре выучены")
                        break
                    }
                    println(question.asConsoleString())

                    print("\nВведите номер ответа: ")
                    val userAnswerInput = readlnOrNull()?.toIntOrNull()
                    if (userAnswerInput == 0) break

                    if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                        println("Правильно!\n")
                    } else {
                        println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translate}\n")
                    }
                }
            }
            Action.STATS.number -> {
                println("Выбран пункт \"${Action.STATS.title}\"")
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%")
            }
            Action.EXIT.number -> {
                println("Выход из программы")
                break
            }
            else ->  println("Введите число 1, 2 или 0")
        }
    }
}


