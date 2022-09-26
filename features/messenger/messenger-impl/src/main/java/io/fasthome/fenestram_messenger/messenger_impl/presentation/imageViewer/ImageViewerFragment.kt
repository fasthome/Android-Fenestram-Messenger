package io.fasthome.fenestram_messenger.messenger_impl.presentation.imageViewer

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentImageViewerBinding
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class ImageViewerFragment : BaseFragment<ImageViewerState, ImageViewerEvent>(R.layout.fragment_image_viewer) {

    private val binding by fragmentViewBinding(FragmentImageViewerBinding::bind)

    override val vm : ImageViewerViewModel by viewModel(
        getParamsInterface = ImageViewerContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnButtonClickListener {
            vm.onBackPressed()
        }
    }

    override fun renderState(state: ImageViewerState) {
        binding.image.apply {
            state.imageBitmap?.let {
                loadRounded(state.imageBitmap, radius = 1)
            }
            state.imageUrl?.let {
                loadRounded(state.imageUrl, radius = 1)
            }
        }
    }

    override fun handleEvent(event: ImageViewerEvent) = noEventsExpected()
}