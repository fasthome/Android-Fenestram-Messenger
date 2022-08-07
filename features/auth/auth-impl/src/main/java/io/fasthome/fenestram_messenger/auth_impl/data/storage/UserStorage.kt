package io.fasthome.fenestram_messenger.auth_impl.data.storage

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.core.data.KeyValueStorage
import io.fasthome.fenestram_messenger.core.data.stored
import io.fasthome.fenestram_messenger.core.exceptions.UnauthorizedException
import io.fasthome.network.tokens.AccessToken
import kotlinx.coroutines.withContext

class UserStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    private val preferencesStorage = persistentStorageFactory.create("user.prefs")

    private var userId: Long? by preferencesStorage.stored("KEY_USER_ID")

    suspend fun getUserId(): Long? =
        withContext(DispatchersProvider.IO) {
            return@withContext userId
        }

    suspend fun setUserId(userId : Long) =
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userId = userId
        }

    suspend fun clearAccessToken() {
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userId = null
        }
    }
}