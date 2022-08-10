package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentInfoappBinding
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.increaseHitArea
import io.fasthome.fenestram_messenger.util.onClick
import org.koin.androidx.viewmodel.ext.android.viewModel


class InfoappFragment: BaseFragment<InfoappState, InfoappEvent>(R.layout.fragment_infoapp) {

    override val vm: InfoappViewModel by viewModel()

    private val binding by fragmentViewBinding (FragmentInfoappBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding){
        super.onViewCreated(view, savedInstanceState)

        ibAppBar.increaseHitArea(8.dp)
        ibAppBar.onClick { vm.backSettings() }




    }

    override fun renderState(state: InfoappState) {

    }

    override fun handleEvent(event: InfoappEvent) {

    }
}