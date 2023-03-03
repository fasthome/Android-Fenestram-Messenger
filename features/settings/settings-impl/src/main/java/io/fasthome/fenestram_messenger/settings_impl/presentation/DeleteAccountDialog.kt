package io.fasthome.fenestram_messenger.settings_impl.presentation

import android.app.Dialog
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.settings_impl.databinding.DialogDeleteAccountBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object DeleteAccountDialog {

    fun create(
        fragment: BaseFragment<*, *>,
        titleText: PrintableText?,
        messageText: PrintableText,
        onAcceptClicked : () -> Unit
    ): Dialog {
        val errorBinding = DialogDeleteAccountBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {
            val theme = fragment.getThemeManager()?.getCurrentTheme() as Theme
            val dialog = DialogBuilder(fragment, theme.bg2Color())
                .addCustomView(root)
                .setCancelable(true)


            title.setTextColor(theme.text0Color())
            description.setTextColor(theme.text0Color())
            cancel.setTextColor(theme.text1Color())
            cancel.background.setTint(theme.bg02Color())
            delete.setTextColor(theme.redColor())
            delete.background.setTint(theme.bg02Color())

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