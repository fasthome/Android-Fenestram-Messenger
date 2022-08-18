package io.fasthome.fenestram_messenger.mvi

sealed class BaseViewEvent<out SE : Any> {

    data class ShowMessage(val message: Message) : BaseViewEvent<Nothing>()

    data class ShowDialog(val message: Message) : BaseViewEvent<Nothing>()

    data class ScreenEvent<SE : Any>(val event: SE) : BaseViewEvent<SE>()
}