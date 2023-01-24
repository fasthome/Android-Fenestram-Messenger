package io.fasthome.fenestram_messenger.messenger_impl.data.storage.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ListStringConverter {
    @TypeConverter
    fun fromString(string: String): List<String> = Json.decodeFromString(string)

    @TypeConverter
    fun toString(list: List<String>): String = Json.encodeToString(list)
}

class ListLongConverter {
    @TypeConverter
    fun fromString(string: String): List<Long> = Json.decodeFromString(string)

    @TypeConverter
    fun toString(list: List<Long>): String = Json.encodeToString(list)
}