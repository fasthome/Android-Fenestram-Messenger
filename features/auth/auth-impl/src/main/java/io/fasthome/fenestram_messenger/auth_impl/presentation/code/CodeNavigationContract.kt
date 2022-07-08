package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object CodeNavigationContract : NavigationContract<NoParams, NoResult>(CodeFragment::class)