/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.util.CallResult

interface AuthRepo {
    suspend fun isUserAuthorized(): CallResult<Boolean>
    suspend fun sendCode(phoneNumber: String, callback: LoginResult.() -> Unit)
    suspend fun login(phoneNumber: String, code: String) : CallResult<LoginResult>
}