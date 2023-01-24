/**
 * Created by Dmitry Popov on 20.01.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.ChatTable

@Dao
interface ChatDao {

    @Query("SELECT * FROM ChatTable")
    suspend fun getChats(): List<ChatTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChats(chats: List<ChatTable>)

    @Query("DELETE FROM ChatTable")
    suspend fun removeChats()
}