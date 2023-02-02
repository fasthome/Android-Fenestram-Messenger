package io.fasthome.component.gallery

import android.net.Uri
import java.io.File

interface GalleryOperations {
    fun getGalleryImages(page: Int, itemsPerPage: Int = GalleryOperationsImpl.IMAGES_COUNT_ON_PAGE): List<GalleryImage>
    fun getGalleryImagesAfter(afterCursorPos: Int, itemsPerPage: Int): List<GalleryImage>
    fun getGalleryImagesBefore(afterCursorPos: Int, itemsPerPage: Int): List<GalleryImage>
    fun getFileFromGalleryImage(image: GalleryImage): File
    fun getFileFromGalleryUri(uri: Uri): File
}