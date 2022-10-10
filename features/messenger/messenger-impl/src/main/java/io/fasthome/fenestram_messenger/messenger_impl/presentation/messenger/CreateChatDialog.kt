package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogCreateChatBinding
import io.fasthome.fenestram_messenger.util.onClick


object CreateChatDialog {
    fun create(
        fragment: Fragment,
        createDialog: (id: Boolean) -> Unit,
    ): Dialog {
        val binding = DialogCreateChatBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = BottomSheetDialogBuilder(fragment)
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

            return dialog.build()
        }
    }
}
