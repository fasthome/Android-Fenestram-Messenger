package io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity

import androidx.annotation.DrawableRes

sealed class File {

    class Document(
        val filename: String
    ) : File()

    class Image(
        @DrawableRes
        val imageRes: Int
    ) : File()

}
