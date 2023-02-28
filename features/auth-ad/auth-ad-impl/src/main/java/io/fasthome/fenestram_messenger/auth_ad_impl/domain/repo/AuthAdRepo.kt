package io.fasthome.fenestram_messenger.auth_ad_impl.domain.repo

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.util.CallResult

interface AuthAdRepo {
    suspend fun login(login: String, password: String): CallResult<LoginResult>
}