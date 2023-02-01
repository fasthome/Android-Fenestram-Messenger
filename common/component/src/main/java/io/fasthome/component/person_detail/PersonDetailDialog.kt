package io.fasthome.component.person_detail

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.R
import io.fasthome.component.databinding.DialogPersonDetailBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.copyTextToClipBoard
import io.fasthome.fenestram_messenger.util.onClick

object PersonDetailDialog {

    private var latestDialog: Dialog? = null

    fun create(
        fragment: Fragment,
        personDetail: PersonDetail,
        launchFaceCallClicked: () -> Unit,
        launchCallClicked: () -> Unit,
        launchConversationClicked: (personDetail: PersonDetail) -> Unit,
        onAvatarClicked: (avatarUrl: String?) -> Unit
    ): Dialog {

        val personDetailBinding = DialogPersonDetailBinding.inflate(fragment.layoutInflater)

        with(personDetailBinding) {

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            name.text = personDetail.userName
            name.setOnClickListener {
                fragment.copyTextToClipBoard(
                    name.text.toString(),
                    PrintableText.StringResource(R.string.common_name_copied)
                )
            }

            phone.text = personDetail.phone
            phone.setOnClickListener {
                fragment.copyTextToClipBoard(
                    phone.text.toString(),
                    PrintableText.StringResource(R.string.common_phone_copied)
                )
            }

            nickname.setOnClickListener {
                fragment.copyTextToClipBoard(
                    nickname.text.toString(),
                    PrintableText.StringResource(R.string.common_nickname_copied)
                )
            }
            if (personDetail.userNickname.isNotEmpty()) {
                nickname.text = "@${personDetail.userNickname}"
            }

            avatar.loadCircle(personDetail.avatar)
            avatar.onClick {
                if (personDetail.avatar.isNotEmpty()) {
                    onAvatarClicked(personDetail.avatar)
                    dialog.dismiss()
                }
            }

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

            latestDialog = dialog.build()
            return latestDialog!!
        }
    }

    fun isShowing() = latestDialog != null && latestDialog!!.isShowing

}