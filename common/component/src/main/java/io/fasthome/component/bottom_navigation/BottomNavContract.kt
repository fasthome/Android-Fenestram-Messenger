package io.fasthome.component.bottom_navigation

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object BottomNavContract : NavigationContract<NoParams, NoResult>(BottomNavFragment::class)