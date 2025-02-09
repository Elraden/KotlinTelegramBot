package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newHttpClient()

    private fun sendRequest(url: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun getUpdates(updateId: Int): String {
        val url = "https://api.telegram.org/bot${botToken}/getUpdates?offset=$updateId"
        return sendRequest(url)
    }

    fun sendMessage(chatId: Long, text: String) {
        val url = "https://api.telegram.org/bot${botToken}/sendMessage?chat_id=$chatId&text=$text"
        sendRequest(url)
    }

}