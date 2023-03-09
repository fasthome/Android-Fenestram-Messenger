package io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.UsersResult
import io.fasthome.fenestram_messenger.util.CallResult

interface AuthAdRepo {
    suspend fun login(username: String, password: String): CallResult<LoginResult>
    suspend fun saveUserId(userId: Long)
    suspend fun getUsers(): CallResult<UsersResult>
    suspend fun logout()
}