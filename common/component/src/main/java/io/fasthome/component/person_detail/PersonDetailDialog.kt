package io.fasthome.component.person_detail

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.R
import io.fasthome.component.databinding.DialogPersonDetailBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.core.ui.extensions.loadAvatarWithGradient
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.copyTextToClipBoard
import io.fasthome.fenestram_messenger.util.onClick

object PersonDetailDialog {

    private var latestDialog: Dialog? = null

    fun create(
        fragment: Fragment,
        theme: Theme,
        personDetail: PersonDetail,
        launchFaceCallClicked: () -> Unit,
        launchCallClicked: () -> Unit,
        launchConversationClicked: (personDetail: PersonDetail) -> Unit,
        onAvatarClicked: (avatarUrl: String?) -> Unit
    ): Dialog {

        val personDetailBinding = DialogPersonDetailBinding.inflate(fragment.layoutInflater)

        with(personDetailBinding) {

            val dialog = BottomSheetDialogBuilder(fragment, theme.bg1Color())
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

            avatar.loadAvatarWithGradient(
                personDetail.avatar,
                username = personDetail.userName
            )
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

            // Theme
            name.setTextColor(theme.text0Color())
            launchConversation.background.setTint(theme.bg2Color())

            latestDialog = dialog.build()
            return latestDialog!!
        }
    }

    fun isShowing() = latestDialog != null && latestDialog!!.isShowing

}