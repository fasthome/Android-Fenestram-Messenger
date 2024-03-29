package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.DepartmentViewItem
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState

data class ContactsState(
    val loadingState: LoadingState<ErrorInfo, List<DepartmentViewItem>>,
)