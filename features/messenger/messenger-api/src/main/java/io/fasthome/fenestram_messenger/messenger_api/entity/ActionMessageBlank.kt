/**
 * Created by Dmitry Popov on 25.01.2023.
 */
package io.fasthome.fenestram_messenger.messenger_api.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ActionMessageBlank : Parcelable {

    @Parcelize
    class Text(
        val text : String
    ) : ActionMessageBlank(), Parcelable

    @Parcelize
    class Image(
        val uri : Uri
    ) : ActionMessageBlank(), Parcelable

}