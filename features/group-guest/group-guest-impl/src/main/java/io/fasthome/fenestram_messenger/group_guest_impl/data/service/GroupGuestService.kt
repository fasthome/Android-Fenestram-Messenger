package io.fasthome.fenestram_messenger.group_guest_impl.data.service

import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper.AddUsersToChatMapper
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatResponse
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.RemoveUsersInChatRequest
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class GroupGuestService(
    clientFactory: NetworkClientFactory,
    private val addUsersToChatMapper: AddUsersToChatMapper,
    private val userStorage: UserStorage
) {
    private val client = clientFactory.create()

    suspend fun addUsersToChat(idChat: Long?, usersId: AddUsersToChatRequest): List<ParticipantsViewItem> {
        val response: BaseResponse<AddUsersToChatResponse> = client.runPatch(
            path = "chats/$idChat/add-user",
            body = usersId
        )

        return addUsersToChatMapper.responseToParticipantsViewItem(response)
    }

    suspend fun deleteUserFromChat(idChat: Long, usersId: RemoveUsersInChatRequest): List<ParticipantsViewItem> {
        val response: BaseResponse<AddUsersToChatResponse> = client.runPatch(
            path = "chats/$idChat/remove-user",
            body = usersId
        )

        return addUsersToChatMapper.responseToParticipantsViewItem(response)
    }

    suspend fun getUserId(): Long? = userStorage.getUserId()
}