/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger.core

import io.fasthome.fenestram_messenger.util.PrintableText

data class ErrorInfo(
    val title: PrintableText,
    val description: PrintableText
)
