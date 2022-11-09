/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

object MessengerNavigationContract : NavigationContract<MessengerNavigationContract.Params, MessengerNavigationContract.Result>(MessengerFragment::class) {
    @Parcelize
    data class Params(
        val chatSelectionMode: Boolean = false
    ) : Parcelable

    sealed class Result : Parcelable {
        @Parcelize
        class ChatSelected(val chatId: Long?,
                           val chatName: PrintableText,
                           val avatar: String?,
                           val isGroup: Boolean,
                           val pendingMessages: PrintableText?) : Result()
    }

}