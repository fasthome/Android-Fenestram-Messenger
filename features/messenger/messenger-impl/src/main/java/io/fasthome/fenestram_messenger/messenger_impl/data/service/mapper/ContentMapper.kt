package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.ContentResponse
import io.fasthome.fenestram_messenger.util.model.MetaInfo

class ContentMapper(
    private val storageUrlConverter: StorageUrlConverter
) {

    fun mapContentResponseToMetaInfo(content: ContentResponse) =
        MetaInfo(
            name = content.name,
            extension = content.extension,
            size = content.size,
            url = storageUrlConverter.convert(content.url)
        )
}