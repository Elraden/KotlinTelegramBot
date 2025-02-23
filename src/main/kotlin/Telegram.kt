package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BASE_URL = "https://api.telegram.org/bot"
const val STATISTICS_CLICKED = "statistics_clicked"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val RESET_CLICKED = "reset_clicked"
const val MESSAGE_ALL_WORDS_LEARNED = "Вы выучили все слова в базе"

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result:List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String? = null,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun checkNextQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long
) {
    val question = trainer.getNextQuestion()
    if (question == null) {
        telegramBotService.sendMessage(chatId, MESSAGE_ALL_WORDS_LEARNED)
    } else {
        telegramBotService.sendQuestion(json, chatId, question)
    }
}

fun handleUpdate(update: Update, json: Json, trainers: HashMap<Long, LearnWordsTrainer>, botService: TelegramBotService) {

    val message = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    if (message?.lowercase() == "/start") {
        botService.sendMenu(json, chatId)
    }

    if (data == STATISTICS_CLICKED ) {
        val statistics = trainer.getStatistics()
        val statisticsString = "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%"
        botService.sendMessage(chatId, statisticsString)
    }

    if (data == LEARN_WORDS_CLICKED) {
        checkNextQuestionAndSend(json, trainer, botService, chatId)
    }

    if (data != null && data.startsWith(CALLBACK_DATA_ANSWER_PREFIX)) {
        val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
        val isCorrectAnswer = trainer.checkAnswer(userAnswerIndex)

        if (isCorrectAnswer) {
            botService.sendMessage(chatId, "Правильно!")
        } else {
            val correctWord = trainer.question?.correctAnswer
            if (correctWord != null) {
                botService.sendMessage(chatId, "Неправильно! ${correctWord.original} – это ${correctWord.translate}")
            }
        }
        checkNextQuestionAndSend(json, trainer, botService, chatId)
    }

    if (data == RESET_CLICKED) {
        trainer.resetProgress()
        botService.sendMessage(chatId, "Прогресс сброшен")
    }
}

fun main(args: Array<String>) {
    val botToken = args[0]
    val botService = TelegramBotService(botToken)
    var lastUpdateId = 0L
    val json = Json {
        ignoreUnknownKeys = true
    }
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = botService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, trainers, botService) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}