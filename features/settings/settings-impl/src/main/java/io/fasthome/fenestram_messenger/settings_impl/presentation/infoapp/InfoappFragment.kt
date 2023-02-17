package io.fasthome.fenestram_messenger.settings_impl.presentation.infoapp

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentInfoappBinding
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText


class InfoappFragment : BaseFragment<InfoappState, InfoappEvent>(R.layout.fragment_infoapp) {

    override val vm: InfoappViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentInfoappBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        hooliToolbar.setOnButtonClickListener {
            onBackPressed()
        }
        hooliLogo.loadCircle(R.drawable.ic_logoholi)

        policyRules.onClick {
            vm.rulesClicked()
        }
    }

    override fun renderState(state: InfoappState) {
        binding.hooliVersion.setPrintableText(state.version)
    }

    override fun handleEvent(event: InfoappEvent) = noEventsExpected()

}