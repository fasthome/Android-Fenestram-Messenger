package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import kotlinx.parcelize.Parcelize


object ContactAddNavigationContract : NavigationContract<ContactAddNavigationContract.Params, ContactAddNavigationContract.ContactAddResult>(ContactAddFragment::class) {
    @Parcelize
    data class Params(
        val name: String? = null,
        val phone: String? = null
    ) : Parcelable


    sealed class ContactAddResult : Parcelable {

        @Parcelize
        object Success : ContactAddResult()

        @Parcelize
        object Canceled : ContactAddResult()
    }
}