package io.fasthome.component.gallery

import io.fasthome.fenestram_messenger.util.CallResult
import java.io.File

interface GalleryRepository {
    suspend fun getGalleryImages(page: Int, itemsPerPage: Int = GalleryRepositoryImpl.IMAGES_COUNT_ON_PAGE): CallResult<List<GalleryImage>>
    suspend fun getFileFromGalleryImage(image: GalleryImage): CallResult<File>
}