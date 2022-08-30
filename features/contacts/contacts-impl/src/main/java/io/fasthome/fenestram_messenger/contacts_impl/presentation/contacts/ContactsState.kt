package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
import io.fasthome.fenestram_messenger.util.PrintableText

data class ContactsState(
    val loadingState: LoadingState<ErrorInfo, List<ContactsViewItem>>,
    val permissionGranted: Boolean
)