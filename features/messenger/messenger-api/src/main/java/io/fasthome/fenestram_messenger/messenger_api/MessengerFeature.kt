/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

interface MessengerFeature {

    val messengerNavigationContract: NavigationContractApi<MessengerParams, NoResult>

    val conversationNavigationContract: NavigationContractApi<Params, MessengerResult>

    suspend fun onChatChanges(
        id: Long,
        chatChangesCallback: (ChatChanges) -> Unit
    )

    suspend fun clearFileStorage()

    suspend fun deleteChat(id: Long): CallResult<Unit>

    suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long,
    ): CallResult<SendMessageResult>

    /**
     * @param chatSelectionMode если true, то экран мессенджера используется для выбора куда переслать сообщение
     * @param forwardMessage пересылаемое сообщение
     */
    @Parcelize
    data class MessengerParams(
        val chatSelectionMode: Boolean = false,
        val forwardMessage: ForwardMessage? = null
    ) : Parcelable

    @Parcelize
    data class ForwardMessage(
        val id: Long,
        val username: PrintableText
    ) : Parcelable

    sealed class MessengerNavResult : Parcelable {
        @Parcelize
        class ChatSelected(
            val chatId: Long?,
            val chatName: PrintableText,
            val avatar: String?,
            val isGroup: Boolean,
            val pendingMessages: PrintableText?,
        ) : MessengerNavResult()

        @Parcelize
        object Canceled : MessengerNavResult()
    }


    @Parcelize
    data class Params(
        val chatId: String? = null,
        val userIds: List<Long>,
        val chatName: String,
        val avatar: String,
        val isGroup: Boolean,
    ) : Parcelable

    sealed class MessengerResult : Parcelable {

        @Parcelize
        object ChatDeleted : MessengerResult()

        @Parcelize
        object Canceled : MessengerResult()
    }

}