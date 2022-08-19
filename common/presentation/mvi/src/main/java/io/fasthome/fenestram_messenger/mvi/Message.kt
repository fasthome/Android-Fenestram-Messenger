package io.fasthome.fenestram_messenger.mvi

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import io.fasthome.fenestram_messenger.util.PrintableText

sealed class Message : Parcelable {

    abstract val id: String
    abstract val messageText: PrintableText

    @Parcelize
    data class Alert(
        override val id: String = ID_NO_MATTER,
        override val messageText: PrintableText,
        val titleText: PrintableText? = null,
        val actionText: PrintableText? = null,
    ) : Message()

    @Parcelize
    data class Dialog(
        override val id: String = ID_NO_MATTER,
        override val messageText: PrintableText,
        val titleText: PrintableText? = null,
        val actionText: PrintableText? = null,
    ) : Message()

    @Parcelize
    data class PopUp(
        override val messageText: PrintableText,
    ) : Message() {
        override val id: String get() = ID_NO_MATTER
    }

    companion object {
        /**
         * Используется в тех случаях, когда Message.id не имеет значения,
         * потому что не предполагается обрабатывать результат этого Message.
         * Результат сообщений со всеми остальными id должен быть обработан в [BaseViewModel.onMessageResult].
         */
        const val ID_NO_MATTER = "no_matter"
    }
}

@Parcelize
data class MessageResult(
    val id: String,
    val action: Action,
) : Parcelable {

    enum class Action {
        Positive, Negative, Canceled;
    }
}

private const val KEY_MESSAGE = "KEY_MESSAGE"
var Bundle.message: Message
    get() = checkNotNull(getParcelable(KEY_MESSAGE))
    set(value) = putParcelable(KEY_MESSAGE, value)

private const val KEY_MESSAGE_RESULT = "KEY_MESSAGE_RESULT"
var Bundle.messageResult: MessageResult
    get() = checkNotNull(getParcelable(KEY_MESSAGE_RESULT))
    set(value) = putParcelable(KEY_MESSAGE_RESULT, value)