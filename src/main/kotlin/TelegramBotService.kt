package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

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
            requestBuilder.build()
        }

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun getUpdates(updateId: Int): String {
        val url = "$TG_URL$botToken/getUpdates?offset=$updateId"
        return sendRequest(url)
    }

    fun sendMessage(chatId: Long, text: String) {
        val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8)
        val url = "$TG_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        sendRequest(url)
    }

    fun sendMenu(chatId: Long) {
        val sendMessage = "$TG_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Основное меню",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Изучить слова",
            					"callback_data": "learn_words_clicked"
            				},
            				{
            					"text": "Статистика",
            					"callback_data": "statistics_clicked"					
            				}	
            			]
            		]
            	}
            }
        """.trimIndent()
        sendRequest(sendMessage, "POST", sendMenuBody)
    }
}