package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogSelectFromBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.UserDropdownBinding
import io.fasthome.fenestram_messenger.util.onClick

object UserMenuDialog {
    fun create(
        fragment: Fragment,
        delete: (id: Long) -> Unit,
        addToContacts: (() -> Unit)? = null,
        id: Long

    ): Dialog {
        val errorBinding = UserDropdownBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            profileGuestCancel.onClick {
                dialog.dismiss()
            }
            profileGuestDeleteUser.onClick {
                delete(id)
                dialog.dismiss()
            }

            profileGuestAddToContacts.onClick{
                dialog.dismiss()
            }

            return dialog.build()
        }
    }
}