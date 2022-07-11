package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.core.data.KeyValueStorage
import io.fasthome.fenestram_messenger.core.data.stored
import kotlinx.coroutines.withContext

class RefreshTokenStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    private val preferencesStorage = persistentStorageFactory.create("tokens_persistent.prefs")

    private var refreshToken: String? by preferencesStorage.stored("KEY_REFRESH_TOKEN")

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