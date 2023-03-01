package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored
import kotlinx.coroutines.withContext

/**
 * Временное решение, предполагается не сохранять access токен, о получать его при входе в приложение через refresh
 */
open class AccessTokenStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    protected val preferencesStorage = persistentStorageFactory.create("tokens_persistent.prefs")

    protected open var accessToken: String? by preferencesStorage.stored("KEY_ACCESS_TOKEN")

    suspend fun getAccessToken(): AccessToken? =
        withContext(DispatchersProvider.IO) {
            return@withContext accessToken?.let(::AccessToken)
        }

    suspend fun setAccessToken(token: AccessToken) =
        withContext(DispatchersProvider.IO) {
            accessToken = token.s
        }

    suspend fun clearAccessToken() {
        withContext(DispatchersProvider.IO) {
            accessToken = null
        }
    }
}

class AccessTokenAdStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) : AccessTokenStorage(persistentStorageFactory) {
    override var accessToken: String? by preferencesStorage.stored("KEY_ACCESS_TOKEN_AD")
}