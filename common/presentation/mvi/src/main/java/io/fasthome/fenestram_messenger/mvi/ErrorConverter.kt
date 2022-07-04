package io.fasthome.fenestram_messenger.mvi

import androidx.annotation.StringRes
import io.fasthome.fenestram_messenger.core.ErrorInfo
import io.fasthome.fenestram_messenger.util.PrintableText

interface ErrorConverter {
    fun convert(throwable: Throwable): ErrorInfo
}

object DefaultErrorConverter : ErrorConverter {
    override fun convert(throwable: Throwable): ErrorInfo {
        val (@StringRes titleStringRes, @StringRes descriptionStringRes) = when (throwable) {
            else -> 0 to 0
        }

        return ErrorInfo(
            title = PrintableText.StringResource(titleStringRes),
            description = PrintableText.StringResource(descriptionStringRes)
        )
    }
}