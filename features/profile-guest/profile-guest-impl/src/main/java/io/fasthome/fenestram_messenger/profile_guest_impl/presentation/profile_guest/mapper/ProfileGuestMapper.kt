package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

object ProfileGuestMapper {

    private const val PREVIEW_IMAGE_SIZE = 6
    private const val PREVIEW_DOCUMENT_SIZE = 3

    fun mapPreviewFilesToRecentImages(files: List<FileItem>): List<RecentImagesViewItem> {
        val previewFiles = files.filterIsInstance<FileItem.Image>()
        val hasMoreImages = previewFiles.size > PREVIEW_IMAGE_SIZE

        return files.take(PREVIEW_IMAGE_SIZE).mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Image) return@mapIndexedNotNull null
            val hasMore = hasMoreImages && index == PREVIEW_IMAGE_SIZE - 1
            val moreImagesCount = files.size - PREVIEW_IMAGE_SIZE
            RecentImagesViewItem(
                image = fileItem.content,
                hasMoreImages = hasMore,
                moreImagesCount = PrintableText.Raw("+$moreImagesCount")
            )
        }
    }

    fun mapPreviewFilesToRecentFiles(files: List<FileItem>): List<RecentFilesViewItem> {
        return files.take(PREVIEW_DOCUMENT_SIZE).mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Document) return@mapIndexedNotNull null
            RecentFilesViewItem(
                path = fileItem.path
            )
        }
    }

    fun mapFilesToRecentImages(files: List<FileItem>): List<RecentImagesViewItem> {
        return files.mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Image) return@mapIndexedNotNull null
            RecentImagesViewItem(
                image = fileItem.content,
                hasMoreImages = false,
                moreImagesCount = PrintableText.EMPTY
            )
        }
    }

}