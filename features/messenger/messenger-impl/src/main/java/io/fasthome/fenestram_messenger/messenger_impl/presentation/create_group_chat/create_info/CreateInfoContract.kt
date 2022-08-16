package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

object CreateInfoContract : NavigationContract<CreateInfoContract.Params, NoResult>(CreateInfoFragment::class) {

    @Parcelize
    data class Params(
        val contacts: List<Contact>
    ) : Parcelable

}