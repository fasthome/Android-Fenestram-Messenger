package io.fasthome.fenestram_messenger.data.prefs

import io.fasthome.fenestram_messenger.data.KeyValueStorage


class InMemoryKeyValueStorage : KeyValueStorage {

    class Factory : KeyValueStorage.Factory {
        override fun create(name: String) = InMemoryKeyValueStorage()
    }

    private val map = mutableMapOf<String, Any?>()

    override fun contains(key: String): Boolean = map.containsKey(key)

    override fun getString(key: String): String? = map[key] as? String

    override fun setString(key: String, value: String?) {
        map[key] = value
    }

    override fun getInt(key: String): Int? = map[key] as? Int

    override fun setInt(key: String, value: Int?) {
        map[key] = value
    }

    override fun getLong(key: String): Long? = map[key] as? Long

    override fun setLong(key: String, value: Long?) {
        map[key] = value
    }

    override fun getBoolean(key: String): Boolean? = map[key] as? Boolean

    override fun setBoolean(key: String, value: Boolean?) {
        map[key] = value
    }

    override fun getByteArray(key: String): ByteArray? = map[key] as? ByteArray

    override fun setByteArray(key: String, value: ByteArray?) {
        map[key] = value
    }

    override fun clear() {
        map.clear()
    }
}