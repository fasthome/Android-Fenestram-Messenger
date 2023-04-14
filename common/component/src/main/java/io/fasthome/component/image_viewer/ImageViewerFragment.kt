package io.fasthome.component.image_viewer

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentImageViewerBinding
import io.fasthome.component.image_viewer.adapter.*
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.custom_view.PreCachingLayoutManager
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.AnimationUtil.loadAnimation
import io.fasthome.fenestram_messenger.util.android.setColor
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.getScreenWidth
import io.fasthome.fenestram_messenger.util.onClick
import kotlinx.coroutines.flow.distinctUntilChanged


class ImageViewerFragment :
    BaseFragment<ImageViewerState, ImageViewerEvent>(R.layout.fragment_image_viewer) {

    private val binding by fragmentViewBinding(FragmentImageViewerBinding::bind)

    private val adapterImages = ImageViewerAdapter(
        onDownSwipe = {
            binding.rvImages.isCanScrolling = false
            binding.root.startAnimation(binding.root.loadAnimation(R.anim.slide_down) {
                vm.onBackPressed()
            })
        }
    )


    private val adapterImagePicker = ImageSliderAdapter(
        onItemClicked = {
            val pos = binding.rvImagesPicker.getChildLayoutPosition(it as ConstraintLayout)
            binding.rvImagesPicker.smoothScrollToPosition(pos)
            binding.rvImages.scrollToPosition(pos)
        }
    )

    override val vm: ImageViewerViewModel by viewModel(
        getParamsInterface = ImageViewerContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibCancel.onClick(vm::onBackPressed)
        binding.ibDelete.onClick(vm::onDeleteImage)
        binding.ibForward.onClick {
            val imagePos =
                (binding.rvImages.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            vm.onForwardImage(imagePos)
        }
        binding.ibDownload.onClick {
            val imagePos =
                (binding.rvImages.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            vm.onDownloadImage(imagePos)
        }
        setupAdapter()
        setupHorizontalPicker()
        subscribeImages()
    }

    override fun syncTheme(appTheme: Theme) = with(binding) {
        super.syncTheme(appTheme)
        appTheme.context = requireContext()
        layout.setBackgroundColor(appTheme.bg1Color())
        llBar.setBackgroundColor(appTheme.bg03Color())
        tvCounter.setTextColor(appTheme.text0Color())
        ibCancel.drawable.setColor(appTheme.ibCancelColor())
        ibForward.drawable.setColor(appTheme.text1Color())
        ibDownload.drawable.setColor(appTheme.text1Color())
        ibDelete.drawable.setColor(appTheme.text1Color())
    }

    private fun subscribeImages() {
        adapterImages.addLoadStateListener { listener ->
            if (listener.prepend.endOfPaginationReached && vm.scrollCursorPosition != null) {
                binding.rvImages.scrollToPosition(vm.scrollCursorPosition!!)
                binding.rvImagesPicker.scrollToPosition(vm.scrollCursorPosition!!)
                vm.scrollCursorPosition = null
            }
        }
        vm.fetchImages()
            .distinctUntilChanged()
            .collectWhenStarted(this@ImageViewerFragment) {
                adapterImages.submitData(this@ImageViewerFragment.lifecycle, it)
                adapterImagePicker.submitData(this@ImageViewerFragment.lifecycle, it)
            }
    }

    private fun setupAdapter() {
        binding.rvImages.adapter = adapterImages
        binding.rvImages.layoutManager =
            PreCachingLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.itemAnimator = null
        binding.rvImages.isNestedScrollingEnabled = false
        PagerSnapHelper().attachToRecyclerView(binding.rvImages)
        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos =
                    (binding.rvImages.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                        ?: return
                if (pos == RecyclerView.NO_POSITION) return
                binding.rvImagesPicker.smoothScrollToPosition(pos)
                vm.updateCounter(pos)
            }
        })
    }

    private fun setupHorizontalPicker() {
        val padding: Int =
            (requireContext().getScreenWidth() - resources.getDimensionPixelSize(R.dimen.image_picker_size)) / 2
        binding.rvImagesPicker.setPadding(padding, 0, padding, 0)
        binding.rvImagesPicker.layoutManager =
            SliderLayoutManager(requireContext()) { scrolledPos ->
                binding.rvImages.scrollToPosition(scrolledPos)
            }
        binding.rvImagesPicker.adapter = adapterImagePicker
    }

    override fun renderState(state: ImageViewerState) {
        binding.tvCounter.isVisible = state.imagesViewerModel.size > 1
        binding.rvImagesPicker.isVisible =
            state.imagesViewerModel.firstOrNull()?.imageGallery != null || state.imagesViewerModel.size > 1
        if (state.imagesViewerModel.size > 1) {
            binding.tvCounter.text = getString(
                R.string.common_value_from_value_ph,
                (state.currPhotoPosition ?: 0) + 1,
                state.imagesViewerModel.size
            )
        }
        binding.ibDelete.isVisible = state.canDelete
        binding.ibForward.isVisible = state.canForward
        binding.ibDownload.isVisible = state.canDownload
    }

    override fun handleEvent(event: ImageViewerEvent) = when (event) {
        is ImageViewerEvent.ToggleProgressEvent -> {
            binding.ibDownload.isEnabled = !event.isProgressVisible
            binding.progress.isVisible = event.isProgressVisible
        }
    }
}