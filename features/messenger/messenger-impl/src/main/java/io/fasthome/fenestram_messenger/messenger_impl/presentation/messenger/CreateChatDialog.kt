package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogCreateChatBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick


object CreateChatDialog {
    fun create(
        fragment: Fragment,
        createDialog: (id: Boolean) -> Unit,
        theme: Theme
    ): Dialog {
        val binding = DialogCreateChatBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = BottomSheetDialogBuilder(fragment,theme.bg0Color())
                .addCustomView(root)
                .setCancelable(true)

            createGroupChat.onClick{
                createDialog(true)
            }

            createPrivateChat.onClick{
                createDialog(false)
            }

            cancel.onClick {
                dialog.dismiss()
            }

            // update Theme
            createPrivateChat.setTextColor(theme.text0Color())
            createGroupChat.setTextColor(theme.text0Color())

            createPrivateChat.background = theme.shapeBg2_10dp()
            createPrivateChat.backgroundTintList = ColorStateList.valueOf(theme.bg2Color())

            createGroupChat.background = theme.shapeBg2_10dp()
            createGroupChat.backgroundTintList = ColorStateList.valueOf(theme.bg2Color())

            return dialog.build()
        }
    }
}
