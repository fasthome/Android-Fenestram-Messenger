package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

object ProfileGuestFilesNavigationContract :
    NavigationContract<NoParams, NoResult>(ProfileGuestFilesFragment::class)