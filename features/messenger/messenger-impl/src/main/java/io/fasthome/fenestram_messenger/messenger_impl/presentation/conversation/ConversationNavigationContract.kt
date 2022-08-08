package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize


object ConversationNavigationContract :
    NavigationContract<ConversationNavigationContract.Params, NoResult>(ConversationFragment::class) {
    @Parcelize
    data class Params(
        val chat: Chat
    ) : Parcelable

}

