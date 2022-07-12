package io.fasthome.fenestram_messenger.core.data

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface KeyValueStorage {

    interface Factory {
        fun create(name: String): KeyValueStorage
    }

    fun contains(key: String): Boolean

    fun getString(key: String): String?
    fun setString(key: String, value: String?)

    fun getInt(key: String): Int?
    fun setInt(key: String, value: Int?)

    fun getLong(key: String): Long?
    fun setLong(key: String, value: Long?)

    fun getBoolean(key: String): Boolean?
    fun setBoolean(key: String, value: Boolean?)

    fun getByteArray(key: String): ByteArray?
    fun setByteArray(key: String, value: ByteArray?)

    fun clear()
}

inline fun <reified E : Enum<E>> KeyValueStorage.stored(key: String, default: E) = object : ReadWriteProperty<Any?, E> {
    private var delegate: String? by stored(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): E =
        runCatching { enumValueOf<E>(delegate!!) }
            .getOrNull()
            ?: default

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: E) {
        delegate = value.name
    }
}

inline fun <reified T> KeyValueStorage.stored(key: String, default: T) = object : ReadWriteProperty<Any?, T> {
    private var delegate: T? by stored(key)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = delegate ?: default

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegate = value
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> KeyValueStorage.stored(key: String): ReadWriteProperty<Any?, T?> =
    when (T::class) {
        String::class -> StringStorageDelegate(this, key)
        Int::class -> IntStorageDelegate(this, key)
        Long::class -> LongStorageDelegate(this, key)
        Boolean::class -> BooleanStorageDelegate(this, key)
        ByteArray::class -> ByteArrayStorageDelegate(this, key)
        else -> error("Unsupported type from storage: ${T::class}")
    } as ReadWriteProperty<Any?, T?>

class StringStorageDelegate(
    private val storage: KeyValueStorage,
    private val key: String,
) : ReadWriteProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? = storage.getString(key)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        storage.setString(key, value)
    }
}

class IntStorageDelegate(
    private val storage: KeyValueStorage,
    private val key: String,
) : ReadWriteProperty<Any?, Int?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int? = storage.getInt(key)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) {
        storage.setInt(key, value)
    }
}

class LongStorageDelegate(
    private val storage: KeyValueStorage,
    private val key: String,
) : ReadWriteProperty<Any?, Long?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long? = storage.getLong(key)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long?) {
        storage.setLong(key, value)
    }
}

class BooleanStorageDelegate(
    private val storage: KeyValueStorage,
    private val key: String,
) : ReadWriteProperty<Any?, Boolean?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean? = storage.getBoolean(key)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) {
        storage.setBoolean(key, value)
    }
}

class ByteArrayStorageDelegate(
    private val storage: KeyValueStorage,
    private val key: String,
) : ReadWriteProperty<Any?, ByteArray?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): ByteArray? =
        storage.getByteArray(key)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: ByteArray?) {
        storage.setByteArray(key, value)
    }
}