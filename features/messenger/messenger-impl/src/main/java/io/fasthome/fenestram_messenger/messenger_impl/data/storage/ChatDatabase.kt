package io.fasthome.fenestram_messenger.messenger_impl.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.fasthome.fenestram_messenger.data.db.ZonedDateTimeConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.converters.ListLongConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.converters.ListStringConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.dao.ChatDao
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.ChatTable

@Database(
    entities = [
        ChatTable::class
    ],
    version = 1,
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    ListStringConverter::class,
    ListLongConverter::class,
)
abstract class ChatDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
}