/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl

import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerNavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.map
import io.fasthome.fenestram_messenger.navigation.model.NoResult

class MessengerFeatureImpl(
    private val messengerInteractor: MessengerInteractor,
) : MessengerFeature {

    override val messengerNavigationContract: NavigationContractApi<MessengerFeature.MessengerParams, NoResult> =
        MessengerNavigationContract.map(
            {
                MessengerNavigationContract.Params(
                    chatSelectionMode = it.chatSelectionMode,
                    forwardMessage = it.forwardMessage,
                    actionMessageBlank = it.newMessage
                )
            },
            resultMapper = { it }
        )

    override val conversationNavigationContract: NavigationContractApi<MessengerFeature.Params, MessengerFeature.MessengerResult> =
        ConversationNavigationContract.map({
            ConversationNavigationContract.Params(
                chat = Chat(
                    id = it.chatId?.toLong(),
                    users = it.userIds,
                    messages = listOf(),
                    time = null,
                    name = it.chatName,
                    avatar = it.avatar,
                    isGroup = it.isGroup,
                    pendingMessages = 0
                )
            )
        },
            resultMapper = {
                when (it) {
                    is ConversationNavigationContract.Result.Canceled -> MessengerFeature.MessengerResult.Canceled
                    is ConversationNavigationContract.Result.ChatDeleted -> MessengerFeature.MessengerResult.ChatDeleted
                }

            })

    override suspend fun onChatChanges(
        id: Long,
        chatChangesCallback: (ChatChanges) -> Unit
    ) {
        messengerInteractor.getChatChanges(id) {
            chatChangesCallback(it)
        }
    }

    override suspend fun clearChats() = messengerInteractor.clearChats()

    override suspend fun deleteChat(id: Long) = messengerInteractor.deleteChat(id)

    override suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long,
    ) =
        messengerInteractor.sendMessage(id, text, type, localId, authorId)


}