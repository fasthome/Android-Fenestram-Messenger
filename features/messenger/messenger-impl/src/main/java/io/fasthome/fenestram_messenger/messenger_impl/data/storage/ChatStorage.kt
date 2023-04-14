/**
 * Created by Dmitry Popov on 24.01.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.data.storage

import io.fasthome.fenestram_messenger.data.db.DatabaseFactory
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.mapper.ChatDbMapper
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat

class ChatStorage(
    databaseFactory: DatabaseFactory,
) {

    private val db by databaseFactory.create(ChatDatabase::class, "ChatDatabase")

    suspend fun getChats(): List<Chat> {
        return db.chatDao.getChats().let(ChatDbMapper::mapTableToChat)
    }

    suspend fun getChat(chatId: Long) = db.chatDao.get(chatId)?.let(ChatDbMapper::mapTableToChat)

    suspend fun saveChat(chat: Chat) = saveChats(listOf(chat))

    suspend fun saveChats(chats: List<Chat>) = db.chatDao.saveChats(chats.let(ChatDbMapper::mapChatToTable))

    suspend fun deleteChats() = db.chatDao.removeChats()

    suspend fun deleteChat(chatId: Long) = db.chatDao.removeChats()

}