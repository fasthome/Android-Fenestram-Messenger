package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState

data class ContactsState(
    val contacts: List<ContactsViewItem>,
    val loadingState: LoadingState<ErrorInfo, List<ContactsViewItem>>
)
