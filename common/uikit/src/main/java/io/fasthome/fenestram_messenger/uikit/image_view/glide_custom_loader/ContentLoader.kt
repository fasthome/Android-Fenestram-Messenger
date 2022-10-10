package io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.signature.ObjectKey
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import java.nio.ByteBuffer
import java.util.*

class ContentLoader : ModelLoader<Content.LoadableContent, ByteBuffer> {
    override fun buildLoadData(
        model: Content.LoadableContent,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<ByteBuffer> {
        return LoadData(ObjectKey(UUID.randomUUID().toString()), ContentFetcher(model))
    }

    override fun handles(model: Content.LoadableContent): Boolean {
        return true
    }
}