package io.fasthome.component.gallery

import android.net.Uri
import io.fasthome.fenestram_messenger.util.CallResult
import java.io.File

interface GalleryRepository {
    fun getGalleryImages(page: Int, itemsPerPage: Int = GalleryRepositoryImpl.IMAGES_COUNT_ON_PAGE): CallResult<List<GalleryImage>>
    fun getGalleryImagesAfter(afterCursorPos: Int, itemsPerPage: Int): CallResult<List<GalleryImage>>
    fun getGalleryImagesBefore(afterCursorPos: Int, itemsPerPage: Int): CallResult<List<GalleryImage>>
    fun getFileFromGalleryImage(image: GalleryImage): CallResult<File>
    fun getFileFromGalleryUri(uri: Uri): CallResult<File>
}