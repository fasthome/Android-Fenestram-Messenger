/**
 * Created by Vladimir Rudakov on 16.02.2023.
 */
package io.fasthome.fenestram_messenger.auth_ad_impl

import io.fasthome.fenestram_messenger.auth_ad_api.AuthAdFeature
import io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login.LoginNavigationContract

class AuthAdFeatureImpl : AuthAdFeature {
    override val authAdNavigationContract = LoginNavigationContract
}