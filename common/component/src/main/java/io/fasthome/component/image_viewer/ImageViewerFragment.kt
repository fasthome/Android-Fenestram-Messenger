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
import io.fasthome.component.image_viewer.adapter.ImageSliderAdapter
import io.fasthome.component.image_viewer.adapter.ImageViewerAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.android.setColor
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.dpToPx
import io.fasthome.fenestram_messenger.util.getScreenWidth
import io.fasthome.fenestram_messenger.util.onClick
import kotlinx.coroutines.flow.distinctUntilChanged

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

    val adapterImagePicker = ImageSliderAdapter(
        onItemClicked = {

            val pos = binding.rvImagesPicker.getChildLayoutPosition(
                it as ConstraintLayout
            )
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
            val imagePos = (binding.rvImages.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            vm.onForwardImage(imagePos)
        }
        binding.ibDownload.onClick(vm::onDownloadImage)

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
                vm.scrollCursorPosition = null
            }
        }
        vm.fetchImages()
            .distinctUntilChanged()
            .collectWhenStarted(this@ImageViewerFragment) {
                adapterImages.submitData(it)
            }
        vm.fetchImages()
            .distinctUntilChanged()
            .collectWhenStarted(this@ImageViewerFragment) {
                adapterImagePicker.submitData(it)
            }
    }

    private fun setupAdapter() {
        binding.rvImages.adapter = adapterImages
        binding.rvImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.itemAnimator = null
        binding.rvImages.isNestedScrollingEnabled = false
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.rvImages)

        binding.rvImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val pos =
                    (binding.rvImages.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                        ?: return
                binding.tvCounter.text = getString(
                    R.string.common_value_from_value_ph,
                    pos + 1,
                    adapterImages.itemCount
                )
            }
        })
    }

    private fun setupHorizontalPicker() {
        val padding: Int = requireContext().getScreenWidth() / 2 - requireContext().dpToPx(42)
        binding.rvImagesPicker.setPadding(padding, 0, padding, 0)

        binding.rvImagesPicker.layoutManager = SliderLayoutManager(requireContext())
        binding.rvImagesPicker.adapter = adapterImagePicker
    }

    override fun renderState(state: ImageViewerState) {
        binding.tvCounter.isVisible = state.imagesViewerModel.size > 1
        if (state.imagesViewerModel.size > 1) {
            binding.tvCounter.text = getString(
                R.string.common_value_from_value_ph,
                (state.currPhotoPosition ?: 0) + 1,
                state.imagesViewerModel.size
            )
        }
        binding.ibDelete.isVisible = state.canDelete
        binding.ibForward.isVisible = state.canForward
    }

    override fun handleEvent(event: ImageViewerEvent) = noEventsExpected()
}