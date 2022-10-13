package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content

sealed class AttachedFile {

    class Image(val content : Content) : AttachedFile()
    class Document(val file: File) : AttachedFile()

}
