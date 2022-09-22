package io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatResponse
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.network.model.BaseResponse
import java.time.ZonedDateTime

class AddUsersToChatMapper(private val environment: Environment) {
    fun responseToParticipantsViewItem(response: BaseResponse<AddUsersToChatResponse>): List<ParticipantsViewItem> {
        response.data?.let { list ->
            return list.chatUsers!!.map { user ->
                ParticipantsViewItem(
                    userId = user.id,
                    name = PrintableText.Raw(user.name ?: user.phone ?: ""),
                    avatar = environment.endpoints.apiBaseUrl.dropLast(1) + user.avatar
                )
            }
        }

        throw Exception()
    }
}