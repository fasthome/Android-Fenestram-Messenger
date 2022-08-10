package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

sealed class PostChatsResult {
    class Success(val chatId : Long) : PostChatsResult()
}