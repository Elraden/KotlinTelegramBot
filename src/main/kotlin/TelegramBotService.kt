package org.example

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newHttpClient()

    private fun sendRequest(url: String, method: String = "GET", body: String? = null): String {
        val requestBuilder = HttpRequest.newBuilder().uri(URI.create(url))

        val request = if (method == "POST" && body != null) {
            requestBuilder
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()
        } else {
            requestBuilder.GET().build()
        }

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun getUpdates(updateId: Long): String {
        val url = "$BASE_URL$botToken/getUpdates?offset=$updateId"
        return sendRequest(url)
    }

    fun sendMessage(chatId: Long, text: String) {
        val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val url = "$BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        sendRequest(url)
    }

    fun sendMenu(json: Json, chatId: Long) {
        val sendMessage = "$BASE_URL$botToken/sendMessage"

        val inlineKeyboardLearnWords = InlineKeyboard(text = "Изучать слова", callbackData = LEARN_WORDS_CLICKED)
        val inlineKeyboardStatistics = InlineKeyboard(text = "Статистика", callbackData = STATISTICS_CLICKED)
        val replyMarkup = ReplyMarkup(listOf(listOf(inlineKeyboardLearnWords, inlineKeyboardStatistics)))
        val requestBody = SendMessageRequest(chatId = chatId,text = "Основное меню", replyMarkup = replyMarkup)

        val requestBodyString = json.encodeToString(requestBody)
        sendRequest(sendMessage, "POST", requestBodyString)
    }

    fun sendQuestion(json: Json, chatId: Long, question: Question) {
        val sendMessage = "$BASE_URL$botToken/sendMessage"

        val replyMarkup = ReplyMarkup(
            listOf(question.variants.mapIndexed { index, word ->
                InlineKeyboard(
                     "$CALLBACK_DATA_ANSWER_PREFIX$index", word.translate,
                )
            })
        )
        val requestBody = SendMessageRequest(chatId, question.correctAnswer.original, replyMarkup)
        val requestBodyString = json.encodeToString(requestBody)

        sendRequest(sendMessage, "POST", requestBodyString)
    }
}