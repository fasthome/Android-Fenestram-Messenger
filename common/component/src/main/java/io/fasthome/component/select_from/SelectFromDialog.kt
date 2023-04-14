package io.fasthome.component.select_from

import android.app.Dialog
import androidx.fragment.app.Fragment
import io.fasthome.component.databinding.DialogSelectFromBinding
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.util.onClick

object SelectFromDialog {

    fun create(
        fragment: Fragment,
        fromGalleryClicked: () -> Unit,
        fromCameraClicked: () -> Unit,
    ): Dialog {
        val errorBinding = DialogSelectFromBinding.inflate(fragment.layoutInflater)

        with(errorBinding) {

            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)

            fromGallery.onClick {
                fromGalleryClicked()
                dialog.dismiss()
            }
            fromCamera.onClick {
                fromCameraClicked()
                dialog.dismiss()
            }
            return dialog.build()
        }
    }

}