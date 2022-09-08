package io.fasthome.fenestram_messenger.util

import java.util.*

object LocaleUtil {
    fun getRuLocale(): Locale {
        Locale.getDefault()
            .takeIf { it.language.equals("ru", ignoreCase = true) }
            ?.let { return it }
        Locale.getAvailableLocales()
            .find { it.language.equals("ru", ignoreCase = true) }
            ?.let { return it }
        return Locale.getDefault() // fallback
    }
}