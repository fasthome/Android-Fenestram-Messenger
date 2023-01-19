package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import io.fasthome.fenestram_messenger.util.PrintableText

data class CodeState(
    val phone : PrintableText,
    /***
     * true если идет загрузка
     */
    val loading: Boolean?,
    /***
     * Throwable != null если ошибка
     */
    val error: Throwable?,
    /***
     * String != null если есть код автоподстановки
     */
    val autoFilling: String?,
)
