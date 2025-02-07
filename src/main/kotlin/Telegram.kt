package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val defaultMessage = "no messages"

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        updateId = Regex("\"update_id\":(\\d+)")
            .find(updates)
            ?.groups
            ?.get(1)?.value
            ?.toInt()
            ?.plus(1) ?: updateId

        val messageText = Regex("\"text\":\"(.+?)\"")
            .find(updates)
            ?.groups
            ?.get(1)?.value ?: defaultMessage

        println(messageText)
    }

}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot${botToken}/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}