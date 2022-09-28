package io.fasthome.fenestram_messenger.data

import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.KeyValueStorage
import io.fasthome.fenestram_messenger.data.stored
import kotlinx.coroutines.withContext

class UserStorage(
    persistentStorageFactory: KeyValueStorage.Factory,
) {
    private val preferencesStorage = persistentStorageFactory.create("user.prefs")

    private var userId: Long? by preferencesStorage.stored("KEY_USER_ID")
    private var userPhone: String? by preferencesStorage.stored("KEY_USER_PHONE")
    private var userCode: String? by preferencesStorage.stored("KEY_USER_CODE")

    suspend fun getUserId(): Long? =
        withContext(DispatchersProvider.IO) {
            return@withContext userId
        }

    suspend fun setUserId(userId : Long) =
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userId = userId
        }

    suspend fun getUserPhone(): String? =
        withContext(DispatchersProvider.IO) {
            return@withContext userPhone
        }

    suspend fun setUserPhone(userPhone : String) =
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userPhone = userPhone
        }

    suspend fun getUserCode(): String? =
        withContext(DispatchersProvider.IO) {
            return@withContext userCode
        }

    suspend fun setUserCode(userCode : String) =
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userCode = userCode
        }

    suspend fun clearAccessToken() {
        withContext(DispatchersProvider.IO) {
            this@UserStorage.userId = null
        }
    }
}