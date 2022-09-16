/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model

import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.util.PrintableText

sealed class AddContactViewItem {

    data class AddContact(
        val userId: Long?,
        val userName: PrintableText
    ) : AddContactViewItem() {
        var isSelected: Boolean = true

        val backgroundRes: Int
            get() = if (isSelected) {
                R.color.dark2
            } else {
                android.R.color.transparent
            }
    }

    class Footer(
        val link: String
    ) : AddContactViewItem()
}