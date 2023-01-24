/**
 * Created by Dmitry Popov on 24.01.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import kotlinx.coroutines.flow.Flow

interface ChatLocalRepo {

    fun observeChats(): Flow<List<Chat>?>

    suspend fun saveChats(chats: List<Chat>)

    suspend fun removeChats()

}