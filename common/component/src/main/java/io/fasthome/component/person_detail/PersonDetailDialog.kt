package io.fasthome.component.person_detail

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogPersonDetailBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.util.onClick

object PersonDetailDialog {

    fun create(
        fragment: Fragment,
        personDetail: PersonDetail,
        launchFaceCallClicked: () -> Unit,
        launchCallClicked: () -> Unit,
        launchConversationClicked: (personDetail: PersonDetail) -> Unit
    ): Dialog {
        val personDetailBinding = DialogPersonDetailBinding.inflate(fragment.layoutInflater)

        with(personDetailBinding) {

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            name.text = personDetail.userName
            phone.text = personDetail.phone
            if (personDetail.userNickname.isNotEmpty())
                nickname.text = "@${personDetail.userNickname}"
            avatar.loadCircle(personDetail.avatar)

            launchFacecall.onClick {
                launchFaceCallClicked()
                dialog.dismiss()
            }
            launchCall.onClick {
                launchCallClicked()
                dialog.dismiss()
            }
            launchConversation.onClick {
                launchConversationClicked(personDetail)
                dialog.dismiss()
            }
            return dialog.build()
        }
    }

}