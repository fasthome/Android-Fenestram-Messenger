package io.fasthome.fenestram_messenger.data.db

import androidx.room.RoomDatabase
import kotlin.reflect.KClass

interface DatabaseFactory {

    /**
     * Создание БД требуемого типа с переданным именем.
     *
     * @param dbClass класс БД
     * @param name имя БД
     */
    fun <T : RoomDatabase> create(dbClass: KClass<T>, name: String) : Lazy<T>
}