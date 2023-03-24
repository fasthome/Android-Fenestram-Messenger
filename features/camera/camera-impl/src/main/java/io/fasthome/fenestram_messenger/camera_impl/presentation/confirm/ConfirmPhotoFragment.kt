package io.fasthome.fenestram_messenger.camera_impl.presentation.confirm

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.camera_impl.R
import io.fasthome.fenestram_messenger.camera_impl.databinding.FragmentConfirmPhotoBinding
import io.fasthome.fenestram_messenger.core.ui.extensions.loadFromFile
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.onClick

class ConfirmPhotoFragment :
    BaseFragment<ConfirmPhotoState, ConfirmPhotoEvent>(R.layout.fragment_confirm_photo) {

    private val binding by fragmentViewBinding(FragmentConfirmPhotoBinding::bind)

    override val vm: ConfirmPhotoViewModel by viewModel(ConfirmPhotoNavigationContract.getParams)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibCancel.onClick(requireActivity()::onBackPressed)
        binding.primaryButton.onClick(vm::onSaveClicked)
        binding.secondaryButton.onClick(vm::onRetakeClicked)
    }

    override fun renderState(state: ConfirmPhotoState): Unit = with(binding) {
        imageView.loadFromFile((state.content as Content.FileContent).file)
    }

    override fun handleEvent(event: ConfirmPhotoEvent) = noEventsExpected()
}