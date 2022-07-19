package io.fasthome.fenestram_messenger.auth_impl.domain.entity

sealed class ProfileResult {
    /**
     * У пользователя нет соединения с интернетом
     */
    object ConnectionError : ProfileResult()

    /**
     * Успешный запрос на получение кода
     */
    object Success : ProfileResult()
}