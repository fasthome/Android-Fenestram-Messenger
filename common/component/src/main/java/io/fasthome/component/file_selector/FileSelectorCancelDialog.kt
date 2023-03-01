package io.fasthome.component.file_selector

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogFileSelectorCancelBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.util.onClick


object FileSelectorCancelDialog {

    fun create(
        fragment: Fragment,
        onAcceptClicked: () -> Unit,
    ): Dialog {
        val binding = DialogFileSelectorCancelBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            cancel.onClick {
                dialog.dismiss()
            }
            delete.onClick {
                onAcceptClicked()
                dialog.dismiss()
            }
            return dialog.build()
        }
    }
}