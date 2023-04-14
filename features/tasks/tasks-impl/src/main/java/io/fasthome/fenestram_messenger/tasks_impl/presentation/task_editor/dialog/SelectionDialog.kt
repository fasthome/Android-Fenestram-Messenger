package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.dialog

import android.app.Dialog
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.tasks_impl.databinding.DialogTaskEditorSelectionBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.adapter.TaskEditorDialogAdapter
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

object SelectionDialog {

    fun create(
        fragment: BaseFragment<*, *>,
        selectionTitle: PrintableText,
        items: List<SelectionViewItem>,
        onItemSelected: (SelectionViewItem) -> Unit,
    ): Dialog {
        val binding = DialogTaskEditorSelectionBinding.inflate(fragment.layoutInflater)

        val theme = fragment.getTheme()

        with(binding) {
            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            title.setPrintableText(selectionTitle)

            val adapter = TaskEditorDialogAdapter(
                onItemClicked = {
                    onItemSelected(it)
                    dialog.dismiss()
                }
            )
            adapter.items = items

            list.adapter = adapter

            dialog.setBackground(theme.bg1Color())
            return dialog.build()
        }
    }
}