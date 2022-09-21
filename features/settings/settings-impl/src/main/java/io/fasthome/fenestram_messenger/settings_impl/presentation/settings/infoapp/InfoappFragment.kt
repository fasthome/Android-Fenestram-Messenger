package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp

import android.os.Bundle
import android.view.View
import coil.load
import coil.transform.CircleCropTransformation
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentInfoappBinding


class InfoappFragment : BaseFragment<InfoappState, InfoappEvent>(R.layout.fragment_infoapp) {

    override val vm: InfoappViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentInfoappBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        hooliToolbar.setOnButtonClickListener {
            onBackPressed()
        }
    }

    override fun renderState(state: InfoappState): Unit = with(binding) {
        hooliLogo.load(R.drawable.ic_logoholi){
            transformations(CircleCropTransformation())
        }

    }
    override fun handleEvent(event: InfoappEvent) = noEventsExpected()
}