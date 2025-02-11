package org.example

const val TG_URL = "https://api.telegram.org/bot"
fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)

    var updateId = 0
    val defaultMessage = "no messages"
    val defaultData = "no data"

    val regexUpdateId = Regex("\"update_id\":(\\d+)")
    val regexMessageText = Regex("\"text\":\"(.+?)\"")
    val regexChatId = Regex("\"chat\":\\{\"id\":(\\d+)")
    val regexData = Regex("\"data\":\"(.+?)\"")

    val trainer = LearnWordsTrainer("words.txt")

    while (true) {
        Thread.sleep(2000)
        val updates: String = botService.getUpdates(updateId)
        println(updates)

        updateId = regexUpdateId
            .find(updates)
            ?.groups
            ?.get(1)?.value
            ?.toIntOrNull()
            ?.plus(1) ?: continue

        val messageText = regexMessageText.find(updates)?.groups?.get(1)?.value ?: defaultMessage

        println(messageText)

        val chatId = regexChatId
            .find(updates)
            ?.groups
            ?.get(1)?.value
            ?.toLongOrNull() ?: continue

        println(chatId)

        val data = regexData.find(updates)?.groups?.get(1)?.value ?: defaultData

        println(data)

        if (messageText.lowercase() == "hello") {
            botService.sendMessage(chatId, "Hello")
        }

        if (messageText.lowercase() == "/start") {
            botService.sendMenu(chatId)
        }
    }

}