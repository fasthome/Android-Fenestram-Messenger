package io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatResponse
import io.fasthome.network.model.BaseResponse
import java.time.ZonedDateTime

class AddUsersToChatMapper(private val environment: Environment) {
    fun responseToParticipantsViewItem(response: BaseResponse<AddUsersToChatResponse>): List<User> {
        response.data?.let { list ->
            return list.chatUsers!!.map { user ->
                User(
                    id = user.id,
                    phone = user.phone,
                    name = user.name ?: "",
                    nickname = user.nickname ?: "",
                    email = user.email ?: "",
                    birth = user.birth ?: "",
                    avatar = environment.endpoints.apiBaseUrl.dropLast(1) + user.avatar,
                    isOnline = user.isOnline ?: true,
                    lastActive = ZonedDateTime.now()
                )
            }
        }

        throw Exception()
    }
}