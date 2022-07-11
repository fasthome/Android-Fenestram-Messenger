package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class PersonalityFragment : BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality) {
    override val vm : PersonalityViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentPersonalityBinding::bind)

    override fun renderState(state: PersonalityState) = with(binding) {
        when(state){
            is PersonalityState.CorrectPersonalityState -> {
                // TODO ПЕРЕХОД НА ГЛАВНУЮ СТРАНИЦУ
            }
            is PersonalityState.UncorrectPersonalityState -> {

            }
            is PersonalityState.BeginCodePersonalityState -> {

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonReady.setOnClickListener {
            vm.checkPersonalData()
        }

        binding.labelSkip.setOnClickListener {
            // TODO ПРОПУСТИТЬ ВВОД ДАННЫх
        }

        binding.nameInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !binding.nameInput.text.toString().isEmpty())
                binding.nameInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_24, 0);
        }
    }

    override fun handleEvent(event: PersonalityEvent): Unit = noEventsExpected()

}