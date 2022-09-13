package io.fasthome.fenestram_messenger.core.ui.dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.databinding.DialogDeleteChatBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object DeleteChatDialog {

    fun create(
        fragment: Fragment,
        titleText: PrintableText?,
        callback: () -> Unit
    ): Dialog {
        val binding = DialogDeleteChatBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(false)

            titleText?.let {
                title.setPrintableText(it)
            }

            delete.onClick {
                callback()
            }

            cancel.onClick {
                dialog.dismiss()
            }
            return dialog.build()
        }
    }
}