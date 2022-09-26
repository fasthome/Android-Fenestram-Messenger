package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
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
        hooliLogo.loadCircle(R.drawable.ic_logoholi)
    }

    override fun renderState(state: InfoappState) = nothingToRender()

    override fun handleEvent(event: InfoappEvent) = noEventsExpected()

}