package io.fasthome.fenestram_messenger.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

fun DateTimeFormatter.parseLocalDate(text: CharSequence): LocalDate = parse(text, LocalDate::from)

fun DateTimeFormatter.parseLocalDateOrNull(text: CharSequence): LocalDate? =
    runCatching { parseLocalDate(text) }.getOrNull()

fun DateTimeFormatter.parseLocalDateTime(text: CharSequence): LocalDateTime =
    parse(text, LocalDateTime::from)

fun DateTimeFormatter.parseZonedDateTime(text: CharSequence): ZonedDateTime =
    parse(text, ZonedDateTime::from)

fun DateTimeFormatter.safeFormat(temporal: TemporalAccessor): String? =
    runCatching {
        format(temporal)
    }.getOrNull()

object DateUtil {

    inline fun forEachDay(
        start: LocalDate,
        endInclusive: LocalDate,
        action: (LocalDate) -> Unit,
    ) {
        check(start <= endInclusive)
        var date = start
        while (date <= endInclusive) {
            action(date)
            date = date.plusDays(1)
        }
    }
}