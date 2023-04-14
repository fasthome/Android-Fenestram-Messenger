package io.fasthome.fenestram_messenger.camera_impl.presentation.camera

import CameraNavigationContract
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import io.fasthome.component.camera.CameraComponentContract
import io.fasthome.component.camera.CameraComponentState
import io.fasthome.fenestram_messenger.camera_impl.R
import io.fasthome.fenestram_messenger.camera_impl.databinding.FragmentCameraBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class CameraFragment : BaseFragment<CameraState, CameraEvent>(R.layout.fragment_camera) {

    private val binding by fragmentViewBinding(FragmentCameraBinding::bind)

    private var orientation: CameraComponentState.Rotation? = null

    private val cameraComponentInterface by registerFragment(
        componentFragmentContractInterface = CameraComponentContract,
        paramsProvider = { CameraNavigationContract.getParams.getParams(this).cameraComponentParams },
        containerViewId = R.id.camera_component_container,
    )

    override val vm: CameraViewModel by viewModel(
        getParamsInterface = CameraNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::cameraComponentInterface)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding){
        super.onViewCreated(view, savedInstanceState)
        captureButton.onClick(vm::onCaptureClicked)
        switchButton.onClick(vm::onSwitchCameraClicked)
        flashButton.onClick(vm::onSwitchFlashClicked)
        ibCancel.onClick(vm::onBackPressed)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        orientation = null
    }

    override fun renderState(state: CameraState) {
        binding.flashButton.isEnabled = state.cameraComponentState.isFlashButtonEnabled
        binding.flashButton.setImageResource(
            when (state.cameraComponentState.flash) {
                CameraComponentState.Flash.Off -> R.drawable.flash_off
                CameraComponentState.Flash.On -> R.drawable.flash_on
                CameraComponentState.Flash.Auto -> R.drawable.flash_auto
                CameraComponentState.Flash.Torch -> R.drawable.flash_torch
            }
        )

        binding.captureProgressBar.isVisible = state.cameraComponentState.inProgress
        binding.captureButton.isEnabled = !state.cameraComponentState.inProgress

        renderRotation(state.cameraComponentState.rotation)
    }

    override fun handleEvent(event: CameraEvent) = noEventsExpected()

    private fun renderRotation(orientation: CameraComponentState.Rotation) {
        if (orientation == this.orientation) return
        this.orientation = orientation

        val degrees = if (orientation.degrees > 180) {
            orientation.degrees - 360
        } else {
            orientation.degrees
        }
        listOf(
            binding.captureButton,
            binding.switchButton,
            binding.flashButton,
        ).forEach { it.animate().rotation(degrees.toFloat()) }

    }
}
