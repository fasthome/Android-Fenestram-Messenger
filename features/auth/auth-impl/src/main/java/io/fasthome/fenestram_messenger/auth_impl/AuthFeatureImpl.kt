/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UserDetail
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.logout.AuthNavigator
import io.fasthome.fenestram_messenger.auth_impl.presentation.logout.LogoutManager
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeNavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.map
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.Flow

class AuthFeatureImpl(
    private val authInteractor: AuthInteractor,
    private val authNavigator: AuthNavigator,
    private val logoutManager: LogoutManager,
) : AuthFeature {

    override val authNavigationContract = WelcomeNavigationContract
    override val personalDataNavigationContract: NavigationContractApi<AuthFeature.PersonalDataParams, AuthFeature.AuthResult> =
        PersonalityNavigationContract.map(
            paramsMapper = {
                PersonalityNavigationContract.Params(
                    userDetail = UserDetail(
                        id = 0,
                        phone = "",
                        name = it.username ?: "",
                        email = it.email ?: "",
                        nickname = it.nickname ?: "",
                        birth = it.birth ?: "",
                        profileImageUrl = it.avatar ?: ""
                    )
                )
            },
            resultMapper = { it }
        )

    override suspend fun getUserId(needLogout: Boolean): CallResult<Long?> =
        authInteractor.getUserId().onSuccess {
            if (it == null && needLogout) {
                logout()
            }
        }

    override suspend fun getUserCode(): CallResult<String?> = authInteractor.getUserCode()

    override suspend fun getUserPhone(): CallResult<String?> = authInteractor.getUserPhone()

    override suspend fun getUsers(): CallResult<List<AuthFeature.User>> = authInteractor.getUsers()

    override val startAuthEvents: Flow<AuthFeature.AuthEvent> get() = authNavigator.startAuthEvents

    override suspend fun isUserAuthorized() = authInteractor.isUserAuthorized()

    override suspend fun logout(needRequest: Boolean): CallResult<Unit> {
        if(needRequest) {
            val result = callForResult {
                authInteractor.logout()
            }.onSuccess {
                return logoutManager.logout()
            }
            return result
        } else return logoutManager.logout()
    }

    override suspend fun login(phone: String, code: String): CallResult<Unit> {
        return when (val result = authInteractor.login(phone, code)) {
            is CallResult.Error -> result
            is CallResult.Success -> CallResult.Success(Unit)
        }
    }

}