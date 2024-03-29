package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent
import io.fasthome.fenestram_messenger.util.PrintableText

object ProfileGuestMapper {

    private const val PREVIEW_DOCUMENT_SIZE = 3

    fun mapPreviewFilesToRecentImages(
        previewCount: Int,
        files: List<FileItem>
    ): List<RecentImagesViewItem> {
        val previewImageSize = if (previewCount == 3) 6 else previewCount
        val previewFiles = files.filterIsInstance<FileItem.Image>()
        val hasMoreImages = previewFiles.size > previewImageSize

        return files.take(previewImageSize).mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Image) return@mapIndexedNotNull null
            val hasMore = hasMoreImages && index == previewImageSize - 1
            val moreImagesCount = files.size - previewImageSize
            RecentImagesViewItem(
                image = fileItem.content as? UrlLoadableContent ?: return@mapIndexedNotNull null,
                hasMoreImages = hasMore,
                moreImagesCount = PrintableText.Raw("+$moreImagesCount")
            )
        }
    }

    fun mapPreviewFilesToRecentFiles(files: List<FileItem>): List<RecentFilesViewItem> {
        return files.take(PREVIEW_DOCUMENT_SIZE).mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Document) return@mapIndexedNotNull null
            RecentFilesViewItem(
                metaInfo = fileItem.metaInfo
            )
        }
    }

    fun mapFilesToRecentImages(files: List<FileItem>): List<RecentImagesViewItem> {
        return files.mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Image) return@mapIndexedNotNull null
            RecentImagesViewItem(
                image = fileItem.content as? UrlLoadableContent ?: return@mapIndexedNotNull null,
                hasMoreImages = false,
                moreImagesCount = PrintableText.EMPTY
            )
        }
    }

    fun mapFilesToRecentDocs(files: List<FileItem>): List<RecentFilesViewItem> {
        return files.mapIndexedNotNull { index, fileItem ->
            if (fileItem !is FileItem.Document) return@mapIndexedNotNull null
            RecentFilesViewItem(
                metaInfo = fileItem.metaInfo
            )
        }
    }

}