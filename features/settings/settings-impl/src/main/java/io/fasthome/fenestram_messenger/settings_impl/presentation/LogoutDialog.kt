package io.fasthome.fenestram_messenger.settings_impl.presentation

import android.app.Dialog
import io.fasthome.fenestram_messenger.core.ui.dialog.DialogBuilder
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.settings_impl.databinding.DialogLogoutBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object LogoutDialog {
    fun create(
        fragment: BaseFragment<*, *>,
        titleText: PrintableText?,
        onAcceptClicked : () -> Unit
    ): Dialog {
        val errorBinding = DialogLogoutBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            val theme = fragment.getThemeManager()?.getCurrentTheme() as Theme

            title.setTextColor(theme.text0Color())
            cancel.setTextColor(theme.text1Color())
            cancel.background = theme.shapeBg02_5dp()
            delete.setTextColor(theme.redColor())
            delete.background = theme.shapeBg02_5dp()
            root.background = theme.shapeBg2_20dp()

            titleText?.let {
                title.setPrintableText(it)
            }

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