package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog

import android.app.Dialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogMessageActionMenuBinding


object MessageActionDialog {

    fun create(
        fragment: Fragment,
        onDelete: (() -> Unit)? = null,
        onEdit: (() -> Unit)? = null,
        onCopy: (() -> Unit)? = null,
    ): Dialog {
        val binding = DialogMessageActionMenuBinding.inflate(fragment.layoutInflater)

        with(binding) {
            deleteMessage.isVisible = onDelete != null
            editMessageText.isVisible = onEdit != null
            copyMessageText.isVisible = onCopy != null

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            deleteMessage.setOnClickListener {
                onDelete?.invoke()
            }
            editMessageText.setOnClickListener {
                onEdit?.invoke()
            }
            copyMessageText.setOnClickListener {
                onCopy?.invoke()
            }

            return dialog.build()
        }
    }
}