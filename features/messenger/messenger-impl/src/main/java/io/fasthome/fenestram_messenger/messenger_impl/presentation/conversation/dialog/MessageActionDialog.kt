package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog

import android.app.Dialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogMessageActionMenuBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.ReactionsAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ReactionViewItem


object MessageActionDialog {

    fun create(
        fragment: Fragment,
        permittedReactions: List<ReactionViewItem>,
        onDelete: (() -> Unit)? = null,
        onEdit: (() -> Unit)? = null,
        onCopy: (() -> Unit)? = null,
        onReply: (() -> Unit)? = null,
        onForward: (() -> Unit)? = null
    ): Dialog {
        val binding = DialogMessageActionMenuBinding.inflate(fragment.layoutInflater)
        val reactionsAdapter = ReactionsAdapter(onItemClicked = {})

        with(binding) {
            deleteMessage.isVisible = onDelete != null
            editMessageText.isVisible = onEdit != null
            copyMessageText.isVisible = onCopy != null
            replyMessage.isVisible = onReply != null
            forwardMessage.isVisible = onForward != null
            listReactions.adapter = reactionsAdapter
            reactionsAdapter.items = permittedReactions

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            deleteMessage.setOnClickListener {
                onDelete?.invoke()
                dialog.dismiss()
            }
            editMessageText.setOnClickListener {
                onEdit?.invoke()
                dialog.dismiss()
            }
            copyMessageText.setOnClickListener {
                onCopy?.invoke()
                dialog.dismiss()
            }
            replyMessage.setOnClickListener {
                onReply?.invoke()
                dialog.dismiss()
            }
            forwardMessage.setOnClickListener {
                onForward?.invoke()
                dialog.dismiss()
            }

            return dialog.build()
        }
    }
}