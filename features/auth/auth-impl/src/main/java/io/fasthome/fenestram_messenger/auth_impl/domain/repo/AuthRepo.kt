/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UsersResult
import io.fasthome.fenestram_messenger.util.CallResult

interface AuthRepo {
    suspend fun isUserAuthorized(): CallResult<Boolean>
    suspend fun getUserId(): CallResult<Long?>
    suspend fun sendCode(phoneNumber: String): CallResult<CodeResult>
    suspend fun login(phoneNumber: String, code: String): CallResult<LoginResult>
    suspend fun saveUserId(userId: Long)
    suspend fun getUsers(): CallResult<UsersResult>
    suspend fun getUserCode(): CallResult<String?>
    suspend fun getUserPhone(): CallResult<String?>
    suspend fun setUserCode(code : String)
    suspend fun setUserPhone(phone : String)
    suspend fun logout()
}