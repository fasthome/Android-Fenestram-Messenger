package io.fasthome.fenestram_messenger.group_guest_impl.data.service

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper.AddUsersToChatMapper
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatResponse
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class GroupGuestService(
    clientFactory: NetworkClientFactory,
    private val addUsersToChatMapper: AddUsersToChatMapper
) {
    private val client = clientFactory.create()

    suspend fun addUsersToChat(idChat: Long?, usersId: List<Long>?): List<User> {
        val response: BaseResponse<AddUsersToChatResponse> = client.runPatch(
            path = "api/v1/chats/$idChat/add-user",
            body = usersId
        )

        return addUsersToChatMapper.responseToParticipantsViewItem(response)
    }

}