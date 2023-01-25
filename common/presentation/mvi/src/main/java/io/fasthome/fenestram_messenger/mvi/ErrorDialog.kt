package io.fasthome.fenestram_messenger.mvi

import android.app.Dialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.mvi.databinding.DialogErrorBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object ErrorDialog {

    fun create(
        fragment: Fragment,
        titleText: PrintableText?,
        messageText: PrintableText,
        onCloseClick: () -> Unit,
        onRetryClick: (() -> Unit)?
    ): Dialog {
        val errorBinding = DialogErrorBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            titleText?.let {
                title.setPrintableText(it)
            }
            description.setPrintableText(messageText)

            close.onClick {
                onCloseClick()
                dialog.dismiss()
            }
            retry.isVisible = onRetryClick != null
            retry.onClick {
                onRetryClick?.invoke()
                dialog.dismiss()
            }

            return dialog.build()
        }
    }

}
