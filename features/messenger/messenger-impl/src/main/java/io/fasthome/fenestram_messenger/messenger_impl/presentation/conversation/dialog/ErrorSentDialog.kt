package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog

import android.app.Dialog
import android.content.res.ColorStateList
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogErrorSentBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick

object ErrorSentDialog {

    fun create(
        fragment: Fragment,
        theme: Theme,
        onRetryClicked: () -> Unit,
        onCancelClicked: () -> Unit
    ): Dialog {
        val errorBinding = DialogErrorSentBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = BottomSheetDialogBuilder(fragment, theme.bg1Color())
                .addCustomView(root)
                .setCancelable(true)

            retry.onClick {
                onRetryClicked()
                dialog.dismiss()
            }
            cancel.onClick {
                onCancelClicked()
                dialog.dismiss()
            }

            // Theme
            retry.setTextColor(theme.text0Color())
            retry.backgroundTintList = ColorStateList.valueOf(theme.bg2Color())
            cancel.setTextColor(theme.text0Color())
            cancel.backgroundTintList = ColorStateList.valueOf(theme.bg2Color())

            return dialog.build()
        }
    }

}