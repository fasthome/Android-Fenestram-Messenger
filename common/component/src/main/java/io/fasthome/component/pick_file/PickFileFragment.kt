package io.fasthome.component.pick_file

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class PickFileFragment : BaseFragment<Unit, Nothing>(), InterfaceFragment<PickFileInterface> {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: PickFileViewModel by viewModel(
        getParamsInterface = PickFileComponentContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    override fun getInterface(): PickFileInterface = vm

    override fun renderState(state: Unit) = nothingToRender()

    override fun handleEvent(event: Nothing) = noEventsExpected()
}