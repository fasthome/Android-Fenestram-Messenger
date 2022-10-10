package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object CreateGroupChatContract : NavigationContract<CreateGroupChatContract.Params, NoResult>(CreateGroupChatFragment::class) {
    @Parcelize
    data class Params(
        val isGroupChat: Boolean
    ) : Parcelable
}