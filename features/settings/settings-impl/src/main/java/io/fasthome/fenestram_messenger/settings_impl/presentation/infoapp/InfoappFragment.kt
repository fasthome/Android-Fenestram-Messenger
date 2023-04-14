package io.fasthome.fenestram_messenger.settings_impl.presentation.infoapp

import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentInfoappBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText


class InfoappFragment : BaseFragment<InfoappState, InfoappEvent>(R.layout.fragment_infoapp) {

    override val vm: InfoappViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentInfoappBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setOnButtonClickListener {
            onBackPressed()
        }
        policyRules.onClick {
            vm.rulesClicked()
        }
    }

    override fun renderState(state: InfoappState) {
        binding.version.setPrintableText(state.version)
    }

    override fun handleEvent(event: InfoappEvent) = noEventsExpected()

    override fun syncTheme(appTheme: Theme) = with(binding){
        root.background = appTheme.bg1Color().toDrawable()
        DrawableCompat.setTint(
            DrawableCompat.wrap(logo.drawable),
            appTheme.logoColor()
        )
        name.setTextColor(appTheme.text0Color())
        version.setTextColor(appTheme.text0Color())
        policyRules.setTextColor(appTheme.text0Color())
    }

}