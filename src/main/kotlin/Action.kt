package org.example

enum class Action(val title: String, val number: Int) {
    LEARN_WORDS("Учить слова", 1),
    STATS("Статистика", 2),
    EXIT("Выход", 0)
}