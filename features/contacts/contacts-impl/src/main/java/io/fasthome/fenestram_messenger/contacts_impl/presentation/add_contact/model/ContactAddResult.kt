package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ContactAddResult : Parcelable {

    @Parcelize
    object Success : ContactAddResult()

    @Parcelize
    object Canceled : ContactAddResult()
}