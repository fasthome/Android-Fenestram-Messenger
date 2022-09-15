/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentAddUserBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import kotlinx.serialization.descriptors.PrimitiveKind


class GroupGuestFragment :
    BaseFragment<GroupGuestState, GroupGuestEvent>(R.layout.fragment_add_user) {

    override val vm: GroupGuestViewModel by viewModel(
        getParamsInterface = GroupGuestContract.getParams
    )

    private val binding by fragmentViewBinding(FragmentAddUserBinding::bind)

    private val contactsAdapter = ContactsAdapter(onItemClicked = {
        vm.onContactClicked(it)
    })

    override fun renderState(state: GroupGuestState) {
        contactsAdapter.items = state.contacts.map { item ->
            item.isSelected = state.addedContacts.find { it.userId == item.userId } != null
            item
        }
        contactsAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnButtonClickListener {
            vm.onBackClick()
        }

        linkField.setOnClickListener {
            vm.onLinkFieldClick(linkField.text.toString())
        }

        listContacts.adapter = contactsAdapter
    }

    override fun handleEvent(event: GroupGuestEvent) {
        when (event) {
            is GroupGuestEvent.CopyTextEvent -> {
                (requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("copy", event.link.replace("\\s".toRegex(), ""))
                )
                Toast.makeText(requireContext(),"Ссылка скопирована",Toast.LENGTH_SHORT).show()
            }
        }
    }


}