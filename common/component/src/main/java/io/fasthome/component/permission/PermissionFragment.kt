package io.fasthome.component.permission

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.booleanArg
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class PermissionFragment : BaseFragment<PermissionState, PermissionEvent>(),
    InterfaceFragment<PermissionInterface> {

    override val vm: PermissionViewModel by viewModel()

    override fun getInterface(): PermissionInterface = vm

    private var permissionRationale by booleanArg("permissionRationale", defaultValue = false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val permission = result.keys.first()

        vm.onPermissionResult(
            permission = permission,
            isGranted = result.getValue(permission),
            shouldShowRequestPermissionRationale = permissionRationale,
        )
    }

    override fun renderState(state: PermissionState) = nothingToRender()

    override fun handleEvent(event: PermissionEvent): Unit = when (event) {
        is PermissionEvent.RequestPermission -> {
            permissionRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(requireActivity(), event.permission)
            requestPermissionLauncher.launch(arrayOf(event.permission))
        }
    }
}