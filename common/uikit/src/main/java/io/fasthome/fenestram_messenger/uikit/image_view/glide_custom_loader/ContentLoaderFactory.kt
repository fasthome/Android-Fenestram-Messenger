package io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader

import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import java.nio.ByteBuffer

class ContentLoaderFactory : ModelLoaderFactory<Content.LoadableContent, ByteBuffer> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Content.LoadableContent, ByteBuffer> =
        ContentLoader()

    override fun teardown() {}
}