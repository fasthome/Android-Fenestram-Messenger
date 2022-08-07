/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.auth_impl.presentation.logout.AuthNavigator
import io.fasthome.fenestram_messenger.auth_impl.presentation.logout.LogoutManager
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeNavigationContract
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.flow.Flow

class AuthFeatureImpl(
    private val authInteractor: AuthInteractor,
    private val authNavigator: AuthNavigator,
    private val logoutManager: LogoutManager,
) : AuthFeature {

    override val authNavigationContract = WelcomeNavigationContract

    override suspend fun getUserId(): CallResult<Long?> = authInteractor.getUserId()

    override val startAuthEvents: Flow<AuthFeature.AuthEvent> get() = authNavigator.startAuthEvents

    override suspend fun isUserAuthorized() = authInteractor.isUserAuthorized()

    override suspend fun logout(): CallResult<Unit> = logoutManager.logout()

}