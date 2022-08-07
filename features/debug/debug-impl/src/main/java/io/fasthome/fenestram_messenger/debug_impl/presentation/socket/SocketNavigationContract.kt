package io.fasthome.fenestram_messenger.debug_impl.presentation.socket

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object SocketNavigationContract : NavigationContract<NoParams, NoResult>(SocketFragment::class)