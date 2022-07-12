package io.fasthome.fenestram_messenger.core.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.KeyScheme
import io.fasthome.fenestram_messenger.core.data.KeyValueStorage

internal class PreferenceKeyValueStorage(
    private val prefs: SharedPreferences,
) : KeyValueStorage {

    class SimpleFactory(private val context: Context) : KeyValueStorage.Factory {
        override fun create(name: String): KeyValueStorage =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
                .let(::PreferenceKeyValueStorage)
    }

    class SecureFactory(private val context: Context) : KeyValueStorage.Factory {
        override fun create(name: String): KeyValueStorage =
            EncryptedSharedPreferences.create(
                context,
                name,
                MasterKey.Builder(context).setKeyScheme(KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            ).let(::PreferenceKeyValueStorage)
    }

    override fun contains(key: String): Boolean =
        prefs.contains(key)

    override fun getString(key: String): String? =
        prefs.getString(key, null)

    override fun setString(key: String, value: String?) {
        putOrRemove(key, value)
    }

    override fun getInt(key: String): Int? =
        prefs.getInt(key, 0).takeIf { prefs.contains(key) }

    override fun setInt(key: String, value: Int?) {
        putOrRemove(key, value)
    }

    override fun getLong(key: String): Long? =
        prefs.getLong(key, 0).takeIf { prefs.contains(key) }

    override fun setLong(key: String, value: Long?) {
        putOrRemove(key, value)
    }

    override fun getBoolean(key: String): Boolean? =
        prefs.getBoolean(key, false).takeIf { prefs.contains(key) }

    override fun setBoolean(key: String, value: Boolean?) {
        putOrRemove(key, value)
    }

    override fun getByteArray(key: String): ByteArray? =
        getString(key)
            ?.let { Base64.decode(it, Base64.DEFAULT) }

    override fun setByteArray(key: String, value: ByteArray?) {
        value
            ?.let { Base64.encodeToString(it, Base64.DEFAULT) }
            .let { setString(key, it) }
    }

    override fun clear() {
        prefs.edit { clear() }
    }

    private inline fun <reified T> putOrRemove(key: String, value: T?) {
        prefs.edit {
            when (value) {
                null -> remove(key)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Boolean -> putBoolean(key, value)
                else -> error("Unsupported type from storage: $value")
            }
        }
    }
}