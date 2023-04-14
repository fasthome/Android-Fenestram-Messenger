package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.adapter

import androidx.core.view.isVisible
import com.dolatkia.animatedThemeManager.ThemeManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.tasks_impl.databinding.HolderTaskEditorDialogBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionViewItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.android.setColor

class TaskEditorDialogAdapter(onItemClicked: (SelectionViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<SelectionViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createStatusDelegate(onItemClicked)
        )
    )

private fun createStatusDelegate(onItemClicked: (SelectionViewItem) -> Unit) =
    adapterDelegateViewBinding<SelectionViewItem, HolderTaskEditorDialogBinding>(
        HolderTaskEditorDialogBinding::inflate
    ) {

        binding.root.onClick {
            onItemClicked(item)
        }

        bindWithBinding {
            selectionValue.setPrintableText(item.selectionName)
            selectionValue.setTextColor((ThemeManager.instance.getCurrentTheme() as Theme).text0Color())

            divider.dividerColor = (ThemeManager.instance.getCurrentTheme() as Theme).stroke2Color()

            arrow.isVisible = item.arrowVisible
            arrow.drawable.setColor((ThemeManager.instance.getCurrentTheme() as Theme).stroke2Color())

            checkbox.isVisible = item.radioButtonVisible
            checkbox.isChecked = item.isChecked
            checkbox.setOnCheckedChangeListener { _, _ ->
                onItemClicked(item)
            }
        }
    }