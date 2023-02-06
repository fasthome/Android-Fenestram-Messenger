package io.fasthome.component.gallery

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import io.fasthome.fenestram_messenger.data.FileSystemInterface
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.android.getFileName
import io.fasthome.fenestram_messenger.util.callForResult
import java.io.File
import java.io.FileOutputStream

class GalleryRepositoryImpl(
    private val appContext: Context,
    private val fileSystemInterface: FileSystemInterface,
) : GalleryRepository {

    companion object {
        const val IMAGES_COUNT_ON_PAGE = 50
        const val IMAGES_COUNT_SMALL_PAGE = 5
        const val IMAGES_COUNT_MEDIUM_PAGE = 15
    }

    override fun getGalleryImagesBefore(
        afterCursorPos: Int,
        itemsPerPage: Int,
    ): CallResult<List<GalleryImage>> = callForResult {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val images = mutableListOf<GalleryImage>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        appContext.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToPosition(afterCursorPos)) cursor.moveToLast()
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext() && images.size != itemsPerPage) {
                images += GalleryImage(
                    uri = ContentUris.withAppendedId(uri, cursor.getLong(idColumn)),
                    cursorPosition = cursor.position
                )

            }
        }
        Log.d("GalleryOperationsImpl",
            "getGalleryImages afterCursorPos: $afterCursorPos, itemsPerPage: $itemsPerPage, loaded images: ${images.size}, firstImageUri: ${images.firstOrNull()}")
        images.reversed()
    }

    override fun getFileFromGalleryUri(uri: Uri): CallResult<File> = getFileFromGalleryImage(GalleryImage(uri,0))


    override fun getFileFromGalleryImage(image: GalleryImage): CallResult<File> = callForResult {
        val tempPhotoFile by lazy { File(fileSystemInterface.cacheDir, "image_${image.uri.getFileName(appContext)}.jpg") }
        try {
            val fos = FileOutputStream(tempPhotoFile)
            fos.write((appContext.contentResolver?.openInputStream(image.uri))?.readBytes())
            fos.close()
        } catch (e: Exception) {
            Log.e("TAG", e.message.toString())
        }
        tempPhotoFile
    }


    override fun getGalleryImagesAfter(afterCursorPos: Int, itemsPerPage: Int): CallResult<List<GalleryImage>> = callForResult {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val images = mutableListOf<GalleryImage>()
        val projection = arrayOf(MediaStore.Images.Media._ID)
        appContext.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToPosition(afterCursorPos)) cursor.moveToLast()
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToPrevious() && images.size != itemsPerPage) {
                images += GalleryImage(
                    uri = ContentUris.withAppendedId(uri, cursor.getLong(idColumn)),
                    cursorPosition = cursor.position
                )

            }
        }
        Log.d("GalleryOperationsImpl",
            "getGalleryImages afterCursorPos: $afterCursorPos, itemsPerPage: $itemsPerPage, loaded images: ${images.size}, firstImageUri: ${images.firstOrNull()}")
        images
    }

    override fun getGalleryImages(page: Int, itemsPerPage: Int): CallResult<List<GalleryImage>> = callForResult {
        var res = emptyList<GalleryImage>()
        if (page >= 0) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val images = mutableListOf<GalleryImage>()
            val projection = arrayOf(MediaStore.Images.Media._ID)
            appContext.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                cursor.moveToLast()
                if (cursor.moveToPosition(cursor.position - itemsPerPage * page)) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    images += GalleryImage(
                        uri = ContentUris.withAppendedId(uri, cursor.getLong(idColumn)),
                        cursorPosition = cursor.position
                    )
                    while (cursor.moveToPrevious() && images.size != itemsPerPage) {
                        images += GalleryImage(
                            uri = ContentUris.withAppendedId(uri, cursor.getLong(idColumn)),
                            cursorPosition = cursor.position
                        )
                    }
                } else {
                    cursor.moveToPosition(cursor.position - itemsPerPage * page - 1)
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (cursor.moveToPrevious()) {
                        images += GalleryImage(
                            uri = ContentUris.withAppendedId(uri, cursor.getLong(idColumn)),
                            cursorPosition = cursor.position
                        )
                    }
                }
            }
            Log.d("GalleryOperationsImpl",
                "getGalleryImages page: $page, itemsPerPage: $itemsPerPage, loaded images: ${images.size}, firstImageUri: ${images.firstOrNull()}")
            res = images
        }
        res
    }
}