package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object GroupGuestContract : NavigationContract<NoParams, NoResult>(GroupGuestBottomFragment::class)