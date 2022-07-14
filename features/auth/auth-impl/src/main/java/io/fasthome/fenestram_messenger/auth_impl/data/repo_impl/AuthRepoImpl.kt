/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_impl.data.service.AuthService
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class AuthRepoImpl(
    private val authService: AuthService
) : AuthRepo {

    override suspend fun isUserAuthorized(): CallResult<Boolean> = callForResult {
        authService.isUserAuthorized()
    }

    override suspend fun sendCode(phoneNumber : String) {
        authService.sendCode(phoneNumber)
    }

    override suspend fun login(phoneNumber: String, code : String){
        authService.login(phoneNumber, code)
    }
}