package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SocketChatChanges(

    @SerialName("data")
    val data: ChatChangesResponse

) {

    @Serializable
    class ChatChangesResponse(

        @SerialName("chatId")
        val chatId: Long?,

        @SerialName("updatedValues")
        val updatedValues: UpdatableValues?
    ) {

        @Serializable
        class UpdatableValues(

            @SerialName("chatUsers")
            val chatUsers: List<UserResponse>? = null,

            @SerialName("users")
            val users: List<Long>? = null
        )
    }
}