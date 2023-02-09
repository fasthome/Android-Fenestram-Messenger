package io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity

import android.os.Parcelable
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import kotlinx.parcelize.Parcelize

sealed class FileItem : Parcelable {

    @Parcelize
    class Document(
        val metaInfo: MetaInfo
    ) : FileItem(), Parcelable

    @Parcelize
    class Image(
        val content: Content
    ) : FileItem(), Parcelable

}
