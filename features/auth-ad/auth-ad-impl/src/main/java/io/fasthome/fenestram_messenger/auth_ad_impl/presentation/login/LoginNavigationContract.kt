package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import io.fasthome.fenestram_messenger.auth_ad_api.AuthAdFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams

object LoginNavigationContract : NavigationContract<NoParams, AuthAdFeature.AuthAdResult>(LoginFragment::class)