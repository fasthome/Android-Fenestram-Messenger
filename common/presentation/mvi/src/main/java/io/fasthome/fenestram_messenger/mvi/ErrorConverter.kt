package io.fasthome.fenestram_messenger.mvi

import android.accounts.NetworkErrorException
import androidx.annotation.StringRes
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.PrintableText
import java.net.ConnectException

interface ErrorConverter {
    fun convert(throwable: Throwable): ErrorInfo
}

object DefaultErrorConverter : ErrorConverter {
    override fun convert(throwable: Throwable): ErrorInfo {
        val (@StringRes titleStringRes, @StringRes descriptionStringRes) = when (throwable) {
            is InternetConnectionException -> R.string.common_error_connection_error_title to R.string.common_error_connection_error_description
            else -> R.string.common_error_something_went_wrong_title to R.string.common_error_something_went_wrong_description
        }

        return ErrorInfo(
            title = PrintableText.StringResource(titleStringRes),
            description = PrintableText.StringResource(descriptionStringRes)
        )
    }
}