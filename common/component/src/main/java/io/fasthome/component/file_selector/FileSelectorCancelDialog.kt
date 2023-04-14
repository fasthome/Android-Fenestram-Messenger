package io.fasthome.component.file_selector

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogFileSelectorCancelBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick


object FileSelectorCancelDialog {

    fun create(
        fragment: Fragment,
        theme: Theme,
        onAcceptClicked: () -> Unit,
    ): Dialog {
        val binding = DialogFileSelectorCancelBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = DialogBuilder(fragment, theme.bg1Color())
                .addCustomView(root)
                .setCancelable(true)

            cancel.onClick {
                dialog.dismiss()
            }
            delete.onClick {
                onAcceptClicked()
                dialog.dismiss()
            }

            title.setTextColor(theme.text0Color())
            description.setTextColor(theme.text0Color())
            cancel.backgroundTintList = ColorStateList.valueOf(theme.text0Color())

            return dialog.build()
        }
    }
}