package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.dialog

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.tasks_impl.databinding.DialogTaskActionMenuBinding


object TasksDialog {

    fun create(
        fragment: BaseFragment<*, *>,
        onEdit: (() -> Unit)? = null,
        onView: (() -> Unit)? = null,
        onChatCustomer: (() -> Unit)? = null,
        onChatExecutor: (() -> Unit)? = null,
        onArchive: (() -> Unit)? = null,
        onDelete: (() -> Unit)? = null,
    ): Dialog {
        val binding = DialogTaskActionMenuBinding.inflate(fragment.layoutInflater)

        val theme = fragment.getTheme()

        with(binding) {
            editTask.isVisible = onEdit != null
            viewTask.isVisible = onView != null
            chatCustomer.isVisible = onChatCustomer != null
            chatExecutor.isVisible = onChatExecutor != null
            archiveTask.isVisible = onArchive != null
            deleteTask.isVisible = onDelete != null

            val dialog = BottomSheetDialogBuilder(fragment, theme.bg1Color())
                .addCustomView(root)
                .setCancelable(true)

            editTask.setOnClickListener {
                onEdit?.invoke()
                dialog.dismiss()
            }
            archiveTask.setOnClickListener {
                onArchive?.invoke()
                dialog.dismiss()
            }
            deleteTask.setOnClickListener {
                onDelete?.invoke()
                dialog.dismiss()
            }
            viewTask.setOnClickListener {
                onView?.invoke()
                dialog.dismiss()
            }
            chatExecutor.setOnClickListener {
                onChatExecutor?.invoke()
                dialog.dismiss()
            }
            chatCustomer.setOnClickListener {
                onChatCustomer?.invoke()
                dialog.dismiss()
            }

            // Theme
            val textColor = theme.text0Color()
            val bgTint = ColorStateList.valueOf(theme.bg2Color())
            editTask.setTextColor(textColor)
            editTask.backgroundTintList = bgTint
            archiveTask.setTextColor(textColor)
            archiveTask.backgroundTintList = bgTint
            viewTask.setTextColor(textColor)
            viewTask.backgroundTintList = bgTint
            chatCustomer.setTextColor(textColor)
            chatCustomer.backgroundTintList = bgTint
            chatExecutor.setTextColor(textColor)
            chatExecutor.backgroundTintList = bgTint
            deleteTask.backgroundTintList = bgTint

            return dialog.build()
        }
    }
}