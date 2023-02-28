package io.fasthome.fenestram_messenger.profile_impl.presentation.profile.dialog

import android.app.Dialog
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.profile_impl.databinding.DialogStatusSelectionBinding
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.adapter.StatusAdapter
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model.StatusViewItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme

object StatusSelectionDialog {

    fun create(
        fragment: BaseFragment<*, *>,
        statusItems : List<StatusViewItem>,
        onStatusSelected: (StatusViewItem) -> Unit,
    ): Dialog {
        val binding = DialogStatusSelectionBinding.inflate(fragment.layoutInflater)

        val theme = fragment.getThemeManager()?.getCurrentTheme() as Theme

        val adapter = StatusAdapter(
            onItemClicked = onStatusSelected
        )
        adapter.items = statusItems

        with(binding) {
            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            list.adapter =  adapter

            dialog.setBackground(theme.bg1Color())
            return dialog.build()
        }
    }
}