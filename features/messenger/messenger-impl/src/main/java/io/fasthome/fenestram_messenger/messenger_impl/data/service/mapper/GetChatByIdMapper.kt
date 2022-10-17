package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatByIdResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.network.model.BaseResponse
import java.time.ZonedDateTime

class GetChatByIdMapper(private val profileImageUrlConverter : StorageUrlConverter) {
    fun responseToGetChatById(response: BaseResponse<GetChatByIdResponse>): GetChatByIdResult {
        response.data?.let {
            return GetChatByIdResult(
                chatUsers = it.chatUsers.map { user->
                    User(
                        id = user.id,
                        phone = user.phone,
                        name = user.name ?: "",
                        nickname = user.nickname ?: "",
                        contactName = user.contactName,
                        email = user.email ?: "",
                        birth = user.birth ?: "",
                        avatar = profileImageUrlConverter.convert(user.avatar),
                        isOnline = true,
                        lastActive = ZonedDateTime.now()
                    )
                },
                avatar = profileImageUrlConverter.convert(it.avatar),
                chatName = it.name ?: ""
            )
        }
        throw Exception()
    }
}