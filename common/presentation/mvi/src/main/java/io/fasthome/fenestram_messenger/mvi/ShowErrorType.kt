package io.fasthome.fenestram_messenger.mvi


sealed interface ShowErrorType {
    object Popup : ShowErrorType
    object Alert : ShowErrorType
    object Dialog : ShowErrorType
}