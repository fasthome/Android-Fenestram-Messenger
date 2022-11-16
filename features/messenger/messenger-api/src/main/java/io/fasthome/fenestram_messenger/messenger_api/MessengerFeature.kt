/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize

interface MessengerFeature {

    val messengerNavigationContract: NavigationContractApi<NoParams, NoResult>

    val conversationNavigationContract: NavigationContractApi<Params, MessengerResult>

    suspend fun onChatChanges(
        id: Long,
        chatChangesCallback: (ChatChanges) -> Unit
    )

    suspend fun deleteChat(id: Long): CallResult<Unit>

    suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long
    ): CallResult<SendMessageResult>

    @Parcelize
    data class Params(
        val chatId: String? = null,
        val userIds: List<Long>,
        val chatName: String,
        val avatar: String,
        val isGroup: Boolean
    ) : Parcelable

    sealed class MessengerResult : Parcelable {

        @Parcelize
        object ChatDeleted : MessengerResult()

        @Parcelize
        object Canceled : MessengerResult()
    }

}