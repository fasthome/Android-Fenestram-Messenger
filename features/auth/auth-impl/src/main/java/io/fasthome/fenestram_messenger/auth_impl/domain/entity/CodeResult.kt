package io.fasthome.fenestram_messenger.auth_impl.domain.entity

sealed class CodeResult {
    /**
     * У пользователя нет соединения с интернетом
     */
    object ConnectionError : CodeResult()

    /**
     * Успешный запрос на получение кода
     */
    object Success : CodeResult()
}