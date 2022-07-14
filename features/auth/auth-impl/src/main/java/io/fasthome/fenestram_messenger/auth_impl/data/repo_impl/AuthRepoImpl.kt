/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_impl.data.service.AuthService
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.repo.AuthRepo
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class AuthRepoImpl(
    private val authService: AuthService
) : AuthRepo {

    override suspend fun isUserAuthorized(): CallResult<Boolean> = callForResult {
        authService.isUserAuthorized()
    }

    override suspend fun sendCode(phoneNumber: String, callback: LoginResult.() -> Unit) {
        authService.sendCode(phoneNumber) {
            callback(this)
        }
    }

    override suspend fun login(
        phoneNumber: String,
        code: String,
        callback: suspend LoginResult.() -> Unit
    ) {
        authService.login(phoneNumber, code) {
            callback(this)
        }
    }

    override suspend fun sendPersonalData(
        personalData: PersonalData,
        callback: Boolean.() -> Unit
    ) {
        authService.sendPersonalData(personalData) {
            callback(this)
        }
    }
}