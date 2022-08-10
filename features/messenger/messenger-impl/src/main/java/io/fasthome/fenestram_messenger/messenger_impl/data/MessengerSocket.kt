package io.fasthome.fenestram_messenger.messenger_impl.data

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketMessage
import io.fasthome.network.tokens.AccessToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URISyntaxException
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

class MessengerSocket(private val baseUrl: String) {

    private var socket: Socket? = null

    fun setClientSocket(token: AccessToken, callback: MessageResponse.() -> Unit) {
        try {
            val opts = IO.Options()
            opts.extraHeaders =
                singletonMap("Authorization", singletonList("Bearer ${token.s}"))
            socket = IO.socket(baseUrl.dropLast(1), opts)
            socket?.connect()
            socket?.on("receiveMessage") {
                Log.d(this.javaClass.simpleName, "receiveMessage: " + it[0].toString())
                val message = Json.decodeFromString<SocketMessage>(it[0].toString())
                callback(with(message.message) {
                    MessageResponse(
                        this?.id?.toLong() ?: 1,
                        this?.initiatorId ?: 1L,
                        this?.text ?: "",
                        this?.type ?: "",
                        this?.date ?: ""
                    )
                })

            }
        } catch (e: URISyntaxException) {
        }
    }

    fun closeClientSocket() {
        socket?.close()
    }
}