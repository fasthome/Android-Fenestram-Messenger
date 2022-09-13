/**
 * Created by Dmitry Popov on 07.09.2022.
 */
package io.fasthome.fenestram_messenger.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", LocaleUtil.getRuLocale())

fun getFuzzyDateString(zonedDateTime: ZonedDateTime?): PrintableText {
    if (zonedDateTime == null) return PrintableText.EMPTY
    val now = ZonedDateTime.now()

    val dayOfYear = zonedDateTime.dayOfYear
    val nowDayOfYear = now.dayOfYear
    return when {
        /**
         * Сегодня
         * */
        dayOfYear == nowDayOfYear -> PrintableText.StringResource(
            R.string.common_today,
            dateFormat.format(zonedDateTime)
        )
        /**
         * Вчера
         * */
        dayOfYear.plus(1) == nowDayOfYear -> PrintableText.StringResource(
            R.string.common_yesterday,
            dateFormat.format(zonedDateTime)
        )
        /**
         * Раньше, чем вчера
         * */
        else -> PrintableText.Raw(dateFormat.format(zonedDateTime))
    }
}