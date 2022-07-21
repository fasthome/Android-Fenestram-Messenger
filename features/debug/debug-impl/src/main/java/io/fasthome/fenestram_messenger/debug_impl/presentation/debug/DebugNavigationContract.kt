package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object DebugNavigationContract : NavigationContract<NoParams, NoResult>(DebugFragment::class)