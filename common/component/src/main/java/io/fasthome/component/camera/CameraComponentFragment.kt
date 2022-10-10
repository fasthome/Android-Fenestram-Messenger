package io.fasthome.component.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Size
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import androidx.annotation.CallSuper
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentCameraComponentBinding
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.android.OrientationEventListenerWithInitial
import io.fasthome.fenestram_messenger.util.doOnStartStop
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CameraComponentFragment :
    BaseFragment<CameraComponentState, CameraComponentEvent>(R.layout.fragment_camera_component),
    InterfaceFragment<CameraComponentInterface> {

    private val binding by fragmentViewBinding(FragmentCameraComponentBinding::bind)

    private val permissionInterface by registerFragment(PermissionComponentContract)

    private var currentCameraType: CameraComponentState.CameraType? = null

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null

    override val vm: CameraComponentViewModel by viewModel(
        getParamsInterface = CameraComponentContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    override fun getInterface(): CameraComponentInterface = vm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val orientationEventListener =
            object : OrientationEventListenerWithInitial(requireContext()) {
                override fun onOrientationChanged(orientation: Int) {
                    vm.onDeviceRotated(orientation)
                }
            }
        lifecycle.doOnStartStop(
            onStart = { orientationEventListener.enable() },
            onStop = { orientationEventListener.disable() },
        )
    }

    @CallSuper
    override fun renderState(state: CameraComponentState) {
        lifecycleScope.launchWhenStarted {
            if (state.cameraType != currentCameraType) {
                ensureActive()
                if (state.cameraType != null) {
                    initPreview(state.cameraType, state.minResolution.toAndroidSize())
                } else {
                    closeCamera()
                }
                currentCameraType = state.cameraType
            }

            val flash = state.flash
            camera?.cameraControl?.enableTorch(flash == CameraComponentState.Flash.Torch)
            imageCapture?.flashMode = flash.flashMode
            imageCapture?.targetRotation = state.rotation.rotationInt
        }
    }

    override fun handleEvent(event: CameraComponentEvent): Unit = when (event) {
        is CameraComponentEvent.Capture -> capture(event)
    }

    private suspend fun closeCamera() = suspendCancellableCoroutine<Unit> { continuation ->
        imageCapture = null
        camera = null

        val cameraFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraFuture.addListener({
            if (continuation.isCancelled) return@addListener

            val cameraProvider = cameraFuture.get()
            cameraProvider.unbindAll()
            continuation.resume(Unit)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private suspend fun initPreview(cameraType: CameraComponentState.CameraType, resolution: Size) =
        suspendCancellableCoroutine<Unit> { continuation ->
            imageCapture = null
            camera = null

            val cameraFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraFuture.addListener({
                if (continuation.isCancelled) return@addListener

                val cameraProvider = cameraFuture.get()
                cameraProvider.unbindAll()

                // todo: проверять минимальное разрешение камеры

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraType.lensFacing)
                    .build()

                if (!cameraProvider.hasCamera(cameraSelector)) {
                    vm.onCameraNotFound()
                    continuation.resume(Unit)
                    return@addListener
                }

                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }

                /**
                 * Экран фотогарфирования всегда показывается в portrait ориентации,
                 * поэтому в targetResolution делаем высоту больше ширины.
                 * В зависимости от поворота девайса меняется [ImageCapture.setTargetRotation],
                 * и фото получается в соответствии с физическим положением девайса
                 * (с учетом [CameraComponentParams.fixedOrientation]).
                 */
                val targetResolution = if (resolution.width > resolution.height) {
                    Size(resolution.height, resolution.width)
                } else {
                    resolution
                }
                val capture = ImageCapture.Builder()
                    .setTargetResolution(targetResolution)
                    .build()

                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, capture)
                controlByTouches(camera)

                this.imageCapture = capture
                this.camera = camera

                vm.onCameraStart(hasFlash = camera.cameraInfo.hasFlashUnit())

                continuation.resume(Unit)
            }, ContextCompat.getMainExecutor(requireContext()))
        }

    private fun capture(event: CameraComponentEvent.Capture) {
        val capture = imageCapture
        if (capture == null) {
            vm.onCaptureError()
            return
        }
        val fileOptions = ImageCapture.OutputFileOptions.Builder(event.file).build()
        capture.takePicture(
            fileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    vm.onCaptured()
                }

                override fun onError(exception: ImageCaptureException) {
                    vm.onCaptureError()
                }
            })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun controlByTouches(camera: Camera) {
        val mainExecutor = ContextCompat.getMainExecutor(requireContext())
        val scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val zoomState = camera.cameraInfo.zoomState.value ?: return true
                    val scale = zoomState.zoomRatio * detector.scaleFactor
                    camera.cameraControl.setZoomRatio(scale)
                        .addListener({ }, mainExecutor)
                    return true
                }
            })

        binding.previewView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                val factory = binding.previewView.meteringPointFactory
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point).build()
                camera.cameraControl.startFocusAndMetering(action)
            }

            return@setOnTouchListener true
        }
    }

    private val CameraComponentState.Flash.flashMode: Int
        get() = when (this) {
            CameraComponentState.Flash.Off -> ImageCapture.FLASH_MODE_OFF
            CameraComponentState.Flash.On -> ImageCapture.FLASH_MODE_ON
            CameraComponentState.Flash.Auto -> ImageCapture.FLASH_MODE_AUTO
            CameraComponentState.Flash.Torch -> ImageCapture.FLASH_MODE_ON
        }

    private val CameraComponentState.CameraType.lensFacing: Int
        get() = when (this) {
            CameraComponentState.CameraType.Back -> CameraSelector.LENS_FACING_BACK
            CameraComponentState.CameraType.Front -> CameraSelector.LENS_FACING_FRONT
        }

    private val CameraComponentState.Rotation.rotationInt: Int
        get() = when (this) {
            CameraComponentState.Rotation.Rotation0 -> Surface.ROTATION_0
            CameraComponentState.Rotation.Rotation90 -> Surface.ROTATION_90
            CameraComponentState.Rotation.Rotation270 -> Surface.ROTATION_270
        }
}