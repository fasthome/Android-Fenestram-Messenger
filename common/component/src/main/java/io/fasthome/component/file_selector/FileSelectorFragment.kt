/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.component.file_selector

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentFileSelectorBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.SingleToast
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.supportBottomSheetScroll
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
        val layoutManager =
            FlexboxLayoutManager(rvImages.context, FlexDirection.ROW, FlexWrap.WRAP)

        rvImages.layoutManager = layoutManager
        rvImages.itemAnimator = null

        rvImages.isNestedScrollingEnabled = false
        rvImages.supportBottomSheetScroll()
    }

}