package io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import kotlinx.coroutines.runBlocking
import java.nio.ByteBuffer

class ContentFetcher(
    private val content: Content.LoadableContent,
) : DataFetcher<ByteBuffer> {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        runBlocking {
            val data = content.load()?.array

            if (data == null) {
                callback.onLoadFailed(Exception("Load content error"))
                return@runBlocking
            }

            val byteBuffer = ByteBuffer.wrap(data)
            callback.onDataReady(byteBuffer)
        }
    }

    override fun cleanup() {}

    override fun cancel() {}

    override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL
}