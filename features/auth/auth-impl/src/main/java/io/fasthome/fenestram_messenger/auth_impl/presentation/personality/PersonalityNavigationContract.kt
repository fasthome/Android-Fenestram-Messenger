package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object PersonalityNavigationContract : NavigationContract<NoParams, AuthFeature.AuthResult>(PersonalityFragment::class)