package io.fasthome.fenestram_messenger.settings_impl.presentation

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.settings_impl.databinding.DialogDeleteAccountBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object DeleteAccountDialog {

    fun create(
        fragment: Fragment,
        titleText: PrintableText?,
        messageText: PrintableText,
        onAcceptClicked : () -> Unit
    ): Dialog {
        val errorBinding = DialogDeleteAccountBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            titleText?.let {
                title.setPrintableText(it)
            }
            description.setPrintableText(messageText)

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