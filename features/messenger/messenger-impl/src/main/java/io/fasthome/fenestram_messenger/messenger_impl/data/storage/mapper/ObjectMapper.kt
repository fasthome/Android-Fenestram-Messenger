package io.fasthome.fenestram_messenger.messenger_impl.data.storage.mapper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ObjectMapper {

    inline fun <reified T> fromString(string: String?): T? = string?.let { Json.decodeFromString(string) }

    inline fun <reified T> toString(message: T): String = Json.encodeToString(message)
}