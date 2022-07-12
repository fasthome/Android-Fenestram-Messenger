/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeNavigationContract
import io.fasthome.fenestram_messenger.util.CallResult

class AuthFeatureImpl(
    private val authInteractor: AuthInteractor,
) : AuthFeature {

    override val authNavigationContract = WelcomeNavigationContract

    override suspend fun isUserAuthorized() = authInteractor.isUserAuthorized()

}