package io.fasthome.component.select_from

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.fasthome.component.R
import io.fasthome.component.databinding.ConversationSelectFromBinding
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryOperationsImpl
import io.fasthome.fenestram_messenger.core.ui.dialog.BottomSheetDialogBuilder
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.onClick

object SelectFromConversation {
    @SuppressLint("SetTextI18n")
    fun create(
        fragment: Fragment,
        attachedContent: List<Content>,
        fromGalleryClicked: () -> Unit,
        fromCameraClicked: () -> Unit,
        attachFileClicked: () -> Unit,
        onLoadImages: (page: Int) -> List<GalleryImage>,
        onImageClicked: (currentImage: GalleryImage) -> Unit,
        onSendMessageClicked: (text: String) -> Unit,
        onAttachFiles: (attachedImage: List<Content>) -> Unit
    ): Dialog {
        val attachedContentGalleryUri = attachedContent.filterIsInstance<UriLoadableContent>().map { it.uri }
        val binding = ConversationSelectFromBinding.inflate(fragment.layoutInflater)
        val selectedList = attachedContent.toMutableList()
        val listAllImages = mutableListOf<GalleryImage>()
        var page = 1
        listAllImages.addAll(onLoadImages(0).map {
            if(attachedContentGalleryUri.contains(it.uri)) it.copy(isChecked = true) else it
        })

        with(binding) {
            val dialog = BottomSheetDialogBuilder(fragment)
                .addCustomView(root)
                .setCancelable(true)
            val buildedDialog = dialog.build()
            rvImages.isVisible = listAllImages.isNotEmpty()

            if (selectedList.isNotEmpty()) {
                clInput.isVisible = true
                    binding.tvCounter.text =
                    fragment.resources.getString(R.string.common_selected) + " " +
                            fragment.resources.getQuantityString(R.plurals.image_quantity,
                                selectedList.size,
                                selectedList.size)
            }

            fromGallery.onClick {
                fromGalleryClicked()
                dialog.dismiss()
            }

            fromCamera.onClick {
                fromCameraClicked()
                dialog.dismiss()
            }

            attachFile.onClick {
                attachFileClicked()
                dialog.dismiss()
            }

            ibCancel.onClick {
                dialog.dismiss()
            }

            sendButton.onClick {
                onSendMessageClicked(inputMessage.text.toString())
                dialog.dismiss()
            }

            val adapterImage = SelectFromConversationAdapter(
                onImageClick = { uri ->
                    onImageClicked(uri)
                },
                onCheckImage = { image ->
                    if (image.isChecked) selectedList.add(UriLoadableContent(image.uri)) else selectedList.removeIf { (it as? UriLoadableContent)?.uri == image.uri }
                    onAttachFiles(selectedList)
                    if (selectedList.isNotEmpty()) {
                        buildedDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                        binding.clInput.isVisible = true
                        binding.tvCounter.text =
                            fragment.resources.getString(R.string.common_selected) + " " +
                                    fragment.resources.getQuantityString(R.plurals.image_quantity,
                                        selectedList.size,
                                        selectedList.size)
                    } else {
                        binding.clInput.isVisible = false
                    }
                }
            )
            rvImages.adapter = adapterImage
            val layoutManager =
                FlexboxLayoutManager(rvImages.context, FlexDirection.ROW, FlexWrap.WRAP)

            rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisiblePos =
                        (rvImages.layoutManager as FlexboxLayoutManager).findLastVisibleItemPosition()
                    if (lastVisiblePos > page * GalleryOperationsImpl.IMAGES_COUNT_ON_PAGE - GalleryOperationsImpl.IMAGES_COUNT_ON_PAGE / 3) {
                        listAllImages.addAll(onLoadImages(page).map {
                            if(attachedContentGalleryUri.contains(it.uri)) it.copy(isChecked = true) else it
                        })
                        page++
                        adapterImage.items = listAllImages
                    }
                }
            })
            rvImages.layoutManager = layoutManager
            rvImages.itemAnimator = null
            rvImages.isNestedScrollingEnabled = true
            adapterImage.items = listAllImages

            buildedDialog.behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // nothing
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    when {
                        slideOffset <= 0f -> {
                            binding.toolbar.isVisible = false
                            binding.clActionButtons.isVisible = true
                        }
                        slideOffset == 1f -> {
                            binding.toolbar.isVisible = true
                            binding.clActionButtons.isVisible = false
                        }
                        slideOffset > 0.01f -> {
                            binding.toolbar.alpha = slideOffset
                            binding.clActionButtons.alpha = 1f - slideOffset
                        }
                    }
                }
            })

            return buildedDialog
        }
    }
}