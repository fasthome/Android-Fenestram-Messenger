package io.fasthome.component.imageViewer

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentImageViewerBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class ImageViewerFragment :
    BaseFragment<ImageViewerState, ImageViewerEvent>(R.layout.fragment_image_viewer) {

    private val binding by fragmentViewBinding(FragmentImageViewerBinding::bind)
    val adapterImages = ImageViewerAdapter(
        onDownSwipe = {
            binding.root.animate().alpha(0f).setDuration(300).start()
            vm.onBackPressed()
        },
        onRootAlphaChanged = { alpha ->
            binding.root.alpha = alpha
        },
        onToggleScroll = { state ->
            binding.rvImages.isCanScrolling = state
        }
    )

    override val vm: ImageViewerViewModel by viewModel(
        getParamsInterface = ImageViewerContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibCancel.onClick(vm::onBackPressed)
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.rvImages.adapter = adapterImages
        binding.rvImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.itemAnimator = null
        binding.rvImages.isNestedScrollingEnabled = false
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.rvImages)
    }

    override fun renderState(state: ImageViewerState) {
        binding.tvCounter.isVisible = state.imagesViewerModel.size > 1
        if (state.imagesViewerModel.size > 1) {
            binding.tvCounter.text = getString(R.string.common_value_from_value_ph,
                (state.currPhotoPosition ?: 0) + 1,
                state.imagesViewerModel.size)
            binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val pos =
                        (binding.rvImages.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                            ?: return
                    binding.tvCounter.text = getString(R.string.common_value_from_value_ph,
                        pos + 1,
                        state.imagesViewerModel.size)
                }
            })
        }
        adapterImages.items = state.imagesViewerModel
        if (state.imagesViewerModel.isNotEmpty()) state.currPhotoPosition?.let {
            binding.rvImages.scrollToPosition(it)
        }
        binding.ibDelete.isVisible = state.canDelete
        binding.ibForward.isVisible = state.canForward
        binding.ibDelete.onClick {
            vm.onDeleteImage()
        }
        binding.ibForward.onClick {
            vm.onForwardImage()
        }
    }

    override fun handleEvent(event: ImageViewerEvent) = noEventsExpected()
}