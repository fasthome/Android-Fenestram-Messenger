/**
 * Created by Dmitry Popov on 24.01.2023.
 */
package io.fasthome.fenestram_messenger.data.db

import androidx.room.TypeConverter
import io.fasthome.fenestram_messenger.util.parseZonedDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeConverter {
    private val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromLocalDate(zonedDateTime: ZonedDateTime?): String? = zonedDateTime?.let(dateFormatter::format)

    @TypeConverter
    fun toLocalDate(string: String?): ZonedDateTime? = string?.let(dateFormatter::parseZonedDateTime)
}