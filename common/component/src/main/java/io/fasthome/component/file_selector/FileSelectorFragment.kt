/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.component.file_selector

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentFileSelectorBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.custom_view.GridAutofitLayoutManager
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

class FileSelectorFragment :
    BaseFragment<FileSelectorState, FileSelectorEvent>(R.layout.fragment_file_selector) {

    override val vm: FileSelectorViewModel by viewModel(
        getParamsInterface = FileSelectorNavigationContract.getParams
    )

    private val binding by fragmentViewBinding(FragmentFileSelectorBinding::bind)

    private var recyclerY = 0f

    private val adapterImage = FileSelectorAdapter(
        onImageClick = { galleryImage ->
            vm.onImageClicked(galleryImage)
        },
        onCheckImage = { image ->
            vm.onAttachFiles(image)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        recyclerY = binding.rvImages.translationY
        setupAdapter()
        fromGallery.onClick {
            vm.fromGalleryClicked()
        }

        fromCamera.onClick {
            vm.fromCameraClicked()
        }

        attachFile.onClick {
            vm.attachFileClicked()
        }

        ibCancel.onClick {
            vm.exitNoResult()
        }
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        val bg03Color = ColorStateList.valueOf(appTheme.bg3Color())
        val text0Color = ColorStateList.valueOf(appTheme.text0Color())
        binding.attachFile.backgroundTintList = bg03Color
        binding.fromGallery.backgroundTintList = bg03Color
        binding.fromCamera.backgroundTintList = bg03Color
        binding.fromGallery.imageTintList = text0Color
        binding.fromCamera.imageTintList = text0Color
        binding.attachFile.imageTintList = text0Color
        binding.clActionButtons.backgroundTintList = ColorStateList.valueOf(appTheme.bg0Color())
        binding.toolbarTitle.setTextColor(appTheme.text0Color())
        binding.toolbar.backgroundTintList = bg03Color
        binding.ibCancel.imageTintList = text0Color
    }

    override fun onResume() {
        super.onResume()
        subscribeImages()
    }

    override fun renderState(state: FileSelectorState) {
        binding.attachFile.isVisible = state.canSelectFiles
    }

    private fun subscribeImages() {
        adapterImage.loadStateFlow.collectWhenStarted(this@FileSelectorFragment) { loadStates ->
            binding.filesProgress.isVisible = loadStates.refresh is LoadState.Loading
        }
        adapterImage.addOnPagesUpdatedListener {
            binding.rvImages.isVisible = adapterImage.itemCount > 0
            binding.tvEmptyView.isVisible = adapterImage.itemCount <= 0
        }
        vm.fetchImages()
            .distinctUntilChanged()
            .collectWhenStarted(this@FileSelectorFragment) {
                binding.root.post {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        delay(200)
                        adapterImage.submitData(it)
                    }
                }
            }
    }

    override fun handleEvent(event: FileSelectorEvent) {
        when (event) {
            is FileSelectorEvent.MaxImagesReached -> {
                SingleToast.show(context, R.string.max_images_attached, Toast.LENGTH_LONG)
            }
            is FileSelectorEvent.ClearAdapterSelect -> {
                adapterImage.snapshot().items.forEach {
                    it.isChecked = it.cursorPosition == event.checkedCursor
                }
                adapterImage.notifyDataSetChanged()
            }
        }
    }

    override fun handleSlideCallback(offset: Float) {
        if (offset !in 0f..1f) return
        binding.toolbar.alpha = offset
        binding.toolbar.isEnabled = offset == 0f
        binding.flBtns.isEnabled = offset == 1f
        binding.flBtns.alpha = 1f - offset * 2.5f
        binding.rvImages.translationY = recyclerY - (binding.fromGallery.height / 1.2f * offset)
    }

    private fun FragmentFileSelectorBinding.setupAdapter() {
        rvImages.adapter = adapterImage
        val layoutManager = GridAutofitLayoutManager(requireContext(), 110.dp)

        rvImages.layoutManager = layoutManager
        rvImages.itemAnimator = null

        rvImages.isNestedScrollingEnabled = false
        rvImages.supportBottomSheetScroll()
    }

}