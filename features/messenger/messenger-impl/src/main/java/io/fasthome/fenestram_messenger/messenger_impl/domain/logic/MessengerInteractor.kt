package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.onSuccess

class MessengerInteractor(
    private val messageRepo: MessengerRepo
) {
    suspend fun sendMessage(id: Int, text: String, type: String) =
        messageRepo.sendMessage(id, text, type).onSuccess { }

    suspend fun getChats(limit: Int, page: Int) = messageRepo.getChats(limit, page).onSuccess { }

    suspend fun postChats(name: String, users: List<Int>) =
        messageRepo.postChats(name, users).onSuccess { }

    suspend fun getChatById(id: Int) = messageRepo.getChatById(id).onSuccess { }
}