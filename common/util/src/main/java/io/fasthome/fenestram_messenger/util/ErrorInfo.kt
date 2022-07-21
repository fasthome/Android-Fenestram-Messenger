/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger.util

import io.fasthome.fenestram_messenger.util.PrintableText

data class ErrorInfo(
    val title: PrintableText,
    val description: PrintableText
) {

    companion object {
        fun createEmpty() = ErrorInfo(PrintableText.Raw("Что-то пошло не так"), PrintableText.EMPTY)
    }
}
