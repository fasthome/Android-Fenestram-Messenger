package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.imageAdapter

import androidx.annotation.DimenRes
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.MetaInfo

data class ImageAdapterItem(
    val metaInfo: MetaInfo,
    val size: ImageItemSizeEnum,
)

enum class ImageItemSizeEnum(@DimenRes val dimenResSize: Int?) {
    AUTO(null), MEDIUM_SIZE(R.dimen.image_medium_min_size), BIG_SIZE(R.dimen.image_big_min_size),
}

fun List<MetaInfo>.createAdapterItems() = when (size) {
    1 -> this.map { ImageAdapterItem(it, ImageItemSizeEnum.BIG_SIZE) }
    2, 4 -> this.map { ImageAdapterItem(it, ImageItemSizeEnum.MEDIUM_SIZE) }
    5 -> this.mapIndexed { index, metaInfo ->  ImageAdapterItem(metaInfo, if (index in 0..1) ImageItemSizeEnum.MEDIUM_SIZE else ImageItemSizeEnum.AUTO) }
    3, 6, 9 -> this.mapIndexed { index, metaInfo ->
        ImageAdapterItem(metaInfo,
            if (index == 0) ImageItemSizeEnum.BIG_SIZE else ImageItemSizeEnum.AUTO)
    }
    10 -> this.mapIndexed { index, metaInfo ->  ImageAdapterItem(metaInfo, if (index in 0..3) ImageItemSizeEnum.MEDIUM_SIZE else ImageItemSizeEnum.AUTO) }
    else -> this.map { ImageAdapterItem(it, ImageItemSizeEnum.AUTO) }
}

