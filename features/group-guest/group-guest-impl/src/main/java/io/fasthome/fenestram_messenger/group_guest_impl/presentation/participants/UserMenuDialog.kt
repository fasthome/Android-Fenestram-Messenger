package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogSelectFromBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.UserDropdownBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick

object UserMenuDialog {
    fun create(
        fragment: Fragment,
        theme: Theme,
        delete: (id: Long) -> Unit,
        addToContacts: ((name: String, phone: String) -> Unit),
        id: Long,
        name: String,
        phone: String

    ): Dialog {
        val errorBinding = UserDropdownBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = BottomSheetDialogBuilder(fragment,theme.bg0Color())
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
                addToContacts(name, phone)
                dialog.dismiss()
            }

            // Theme
            val textColor = theme.text0Color()
            val bgTint = ColorStateList.valueOf(theme.bg2Color())
            profileGuestDeleteUser.setTextColor(textColor)
            profileGuestAddToContacts.setTextColor(textColor)
            profileGuestDeleteUser.backgroundTintList = bgTint
            profileGuestAddToContacts.backgroundTintList = bgTint

            return dialog.build()
        }
    }
}