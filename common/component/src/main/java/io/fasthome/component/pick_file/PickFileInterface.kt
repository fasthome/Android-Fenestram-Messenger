package io.fasthome.component.pick_file

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

interface PickFileInterface {

    sealed interface ResultEvent {
        object PickCancelled : ResultEvent
        data class PickedImage(val tempFile: File) : ResultEvent
        data class PickedFile(val tempFile: File) : ResultEvent
    }

    fun pickFile(mimeType: PickFileComponentParams.MimeType? = null)

    fun launchCamera()

    fun resultEvents(): Flow<ResultEvent>

    fun processUri(uri : Uri)
}