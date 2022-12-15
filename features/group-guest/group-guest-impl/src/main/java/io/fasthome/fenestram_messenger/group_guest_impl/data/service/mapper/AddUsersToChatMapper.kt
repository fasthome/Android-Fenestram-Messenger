package io.fasthome.fenestram_messenger.group_guest_impl.data.service.mapper

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.group_guest_impl.data.service.model.AddUsersToChatResponse
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry
import io.fasthome.network.model.BaseResponse

class AddUsersToChatMapper(private val profileImageUrlConverter: StorageUrlConverter) {
    fun responseToParticipantsViewItem(response: BaseResponse<AddUsersToChatResponse>): List<ParticipantsViewItem> {
        response.data?.let { list ->
            return list.chatUsers!!.map { user ->
                val userName = when {
                    !user.contactName.isNullOrEmpty() -> user.contactName
                    !user.name.isNullOrEmpty() -> user.name
                    !user.nickname.isNullOrEmpty() -> user.nickname
                    else -> user.phone.setMaskByCountry(Country.RUSSIA)
                }
                ParticipantsViewItem(
                    userId = user.id,
                    name = PrintableText.Raw(userName),
                    avatar = profileImageUrlConverter.convert(user.avatar),
                    nickname = user.nickname ?: "",
                    phone = user.phone
                )
            }
        }

        throw Exception()
    }
}