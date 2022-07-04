/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeNavigationContract

class AuthFeatureImpl : AuthFeature {

    override val authNavigationContract = WelcomeNavigationContract

}