package io.fasthome.component.imageViewer

import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.resource.bitmap.FitCenter
import io.fasthome.component.R
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.component.databinding.FragmentImageViewerBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.toIntAlpha
import kotlin.math.roundToInt

class ImageViewerFragment : BaseFragment<ImageViewerState, ImageViewerEvent>(R.layout.fragment_image_viewer) {

    private val binding by fragmentViewBinding(FragmentImageViewerBinding::bind)

    override val vm : ImageViewerViewModel by viewModel(
        getParamsInterface = ImageViewerContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ibCancel.onClick(vm::onBackPressed)
        binding.image.setOnSwipeDownListener(vm::onBackPressed)
        binding.image.setOnAlphaChangedListener { alpha ->
            view.background.alpha = alpha.toIntAlpha()
        }
    }

    override fun renderState(state: ImageViewerState) {
        binding.image.apply {
            state.imageBitmap?.let {
                loadRounded(state.imageBitmap, radius = 1, transform = FitCenter())
            }
            state.imageUrl?.let {
                loadRounded(state.imageUrl, radius = 1,  transform = FitCenter())
            }
        }
    }

    override fun handleEvent(event: ImageViewerEvent) = noEventsExpected()
}