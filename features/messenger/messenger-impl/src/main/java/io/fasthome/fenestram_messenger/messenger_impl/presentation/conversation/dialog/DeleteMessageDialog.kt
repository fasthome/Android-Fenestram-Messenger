package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogDeleteMessageBinding
import io.fasthome.fenestram_messenger.util.onClick

object DeleteMessageDialog {

    fun create(
        fragment: Fragment,
        onDelete: (fromAll: Boolean) -> Unit,
    ): Dialog {
        val dialogBinding = DialogDeleteMessageBinding.inflate(fragment.layoutInflater)

        with(dialogBinding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            deleteFromSelf.onClick {
                onDelete(false)
                dialog.dismiss()
            }

            deleteFromAll.onClick {
                onDelete(true)
                dialog.dismiss()
            }

            cancel.onClick {
                dialog.dismiss()
            }

            return dialog.build()
        }
    }
}