package io.fasthome.component.pick_file

import kotlinx.coroutines.flow.Flow
import java.io.File

interface PickFileInterface {

    sealed interface ResultEvent {
        object PickCancelled : ResultEvent
        data class Picked(val tempFile: File) : ResultEvent
    }

    fun pickFile(mimeType: PickFileComponentParams.MimeType? = null)

    fun launchCamera()

    fun resultEvents(): Flow<ResultEvent>
}