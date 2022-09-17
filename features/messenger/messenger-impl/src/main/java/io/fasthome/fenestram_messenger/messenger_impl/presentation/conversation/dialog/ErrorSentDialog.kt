package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.messenger_impl.databinding.DialogErrorSentBinding
import io.fasthome.fenestram_messenger.util.onClick

object ErrorSentDialog {

    fun create(
        fragment: Fragment,
        onRetryClicked: () -> Unit,
        onCancelClicked: () -> Unit
    ): Dialog {
        val errorBinding = DialogErrorSentBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = BottomSheetDialogBuilder(fragment)
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
            return dialog.build()
        }
    }

}