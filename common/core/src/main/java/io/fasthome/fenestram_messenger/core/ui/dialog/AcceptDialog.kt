package io.fasthome.fenestram_messenger.core.ui.dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.core.databinding.DialogDeleteChatBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

object AcceptDialog {

    fun create(
        fragment: Fragment,
        titleText: PrintableText?,
        accept: (id: Long) -> Unit,
        id : Long,
        acceptButtonRes : Int = R.string.common_delete_chat,
    ): Dialog {
        val binding = DialogDeleteChatBinding.inflate(fragment.layoutInflater)

        with(binding) {

            val dialog = DialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            titleText?.let {
                title.setPrintableText(it)
            }

            delete.setText(acceptButtonRes)
            delete.onClick {
                accept(id)
                dialog.dismiss()
            }

            cancel.onClick {
                dialog.dismiss()
            }
            return dialog.build()
        }
    }
}