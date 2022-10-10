package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
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
        val messengerInteractor = get<MessengerInteractor>()

        val fileData = messengerInteractor.getFile(itemId).getOrNull()

        return fileData?.byteArrayWrapper
    }
}