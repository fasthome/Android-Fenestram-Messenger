/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentCreateInfoChatBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class CreateInfoFragment : BaseFragment<CreateInfoState, CreateInfoEvent>(R.layout.fragment_create_info_chat) {

    override val vm : CreateInfoViewModel by viewModel(
        getParamsInterface = CreateInfoContract.getParams
    )

    private val binding by fragmentViewBinding(FragmentCreateInfoChatBinding::bind)

    private val adapter = ContactsAdapter(onItemClicked = {

    }, selectActive = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        listContacts.adapter = adapter
        toolbar.setOnButtonClickListener {
            onBackPressed()
        }
        next.onClick {
            vm.onReadyClicked(chatName.text.toString())
        }
    }

    override fun renderState(state: CreateInfoState) {
        adapter.items = state.contacts
    }

    override fun handleEvent(event: CreateInfoEvent) = noEventsExpected()

}