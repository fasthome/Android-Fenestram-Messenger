package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object WelcomeNavigationContract : NavigationContract<NoParams, AuthFeature.AuthResult>(WelcomeFragment::class)