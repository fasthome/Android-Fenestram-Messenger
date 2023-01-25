package io.fasthome.fenestram_messenger.camera_api

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.android.ByteArrayWrapper
import io.fasthome.fenestram_messenger.util.getOrNull
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Parcelize
data class PhotoLoadableContent(
    val itemId: String,
) : Content.LoadableContent, KoinComponent {

    override suspend fun load(): ByteArrayWrapper? {
        val filesRepo = get<FilesRepo>()

        val fileData = filesRepo.getFile(itemId).getOrNull()

        return fileData?.byteArrayWrapper
    }
}