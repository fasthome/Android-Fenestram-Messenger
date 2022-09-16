package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import android.graphics.Bitmap
import java.io.File

sealed class AttachedFile {

    class Image(val bitmap : Bitmap, val file : File) : AttachedFile()

}
