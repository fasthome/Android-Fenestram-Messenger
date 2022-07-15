package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsFragment
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult


object ContactAddNavigationContract : NavigationContract<NoParams, NoResult>(ContactAddFragment::class)