package io.fasthome.fenestram_messenger.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.reflect.KClass

class SimpleDbFactory(private val context: Context) : DatabaseFactory {

    override fun <T : RoomDatabase> create(dbClass: KClass<T>, name: String): Lazy<T> = lazy {
        Room.databaseBuilder(context, dbClass.java, name).fallbackToDestructiveMigration().build()
    }
}