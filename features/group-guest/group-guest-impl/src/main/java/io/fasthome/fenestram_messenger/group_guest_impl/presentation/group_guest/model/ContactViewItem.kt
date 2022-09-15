/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.util.PrintableText

data class ContactViewItem(
    val userId : Long?,
    val userName : PrintableText
) {
    var isSelected : Boolean = true

    val backgroundRes : Int
        get() = if(isSelected){
            R.color.dark2
        }else{
            android.R.color.transparent
        }
}