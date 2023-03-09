package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored
import kotlinx.coroutines.withContext

open class RefreshTokenStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    protected val preferencesStorage = persistentStorageFactory.create("tokens_persistent.prefs")

    protected open var refreshToken: String? by preferencesStorage.stored("KEY_REFRESH_TOKEN")

    suspend fun getRefreshToken(): RefreshToken? =
        withContext(DispatchersProvider.IO) {
            return@withContext refreshToken?.let(::RefreshToken)
        }

    suspend fun setRefreshToken(token: RefreshToken) =
        withContext(DispatchersProvider.IO) {
            refreshToken = token.s
        }

    suspend fun clearRefreshToken() {
        withContext(DispatchersProvider.IO) {
            refreshToken = null
        }
    }
}

class RefreshTokenAdStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) : RefreshTokenStorage(persistentStorageFactory) {
    override var refreshToken: String? by preferencesStorage.stored("KEY_REFRESH_TOKEN_AD")
}