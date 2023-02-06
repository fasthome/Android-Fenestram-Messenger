/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentFileSelectorBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.supportBottomSheetScroll
import kotlinx.coroutines.flow.distinctUntilChanged

class FileSelectorFragment :
    BaseFragment<FileSelectorState, FileSelectorEvent>(R.layout.fragment_file_selector) {

    override val vm: FileSelectorViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentFileSelectorBinding::bind)

    private val selectedList = mutableListOf<Content>()

    private val adapterImage = FileSelectorAdapter(
        onImageClick = { galleryImage ->
            vm.onImageClicked(galleryImage)
        },
        onCheckImage = { image ->
            if (image.isChecked) selectedList.add(UriLoadableContent(image.uri)) else selectedList.removeIf { (it as? UriLoadableContent)?.uri == image.uri }
            vm.onAttachFiles(selectedList)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        recyclerY = binding.rvImages.translationY
        setupAdapter()
        fromGallery.onClick {
            vm.fromGalleryClicked()
            vm.onBackPressed()
        }

        fromCamera.onClick {
            vm.fromCameraClicked()
            vm.onBackPressed()
        }

        attachFile.onClick {
            vm.attachFileClicked()
            vm.onBackPressed()
        }

        ibCancel.onClick {
            vm.exitNoResult()
        }
        subscribeImages()
    }

    override fun renderState(state: FileSelectorState) {
        binding.rvImages.isVisible = state.images.isNotEmpty()
    }

    private fun subscribeImages() {
        vm.fetchImages()
            .distinctUntilChanged()
            .collectWhenStarted(this@FileSelectorFragment) {
                adapterImage.submitData(it)
            }
    }

    override fun handleEvent(event: FileSelectorEvent) {

    }


    private var recyclerY = 0f
    override fun handleSlideCallback(offset: Float) {
        if (offset !in 0f..1f) return
        binding.toolbar.alpha = offset
        binding.toolbar.isEnabled = offset == 0f
        binding.clActionButtons.isEnabled = offset == 1f
        binding.clActionButtons.alpha = 1f - offset * 2.5f
        binding.rvImages.translationY = recyclerY - (binding.clActionButtons.height / 2f * offset)
    }

    private fun FragmentFileSelectorBinding.setupAdapter() {
        rvImages.adapter = adapterImage
        val layoutManager =
            FlexboxLayoutManager(rvImages.context, FlexDirection.ROW, FlexWrap.WRAP)

        rvImages.layoutManager = layoutManager
        rvImages.itemAnimator = null

        rvImages.isNestedScrollingEnabled = false
        rvImages.supportBottomSheetScroll()
    }

}