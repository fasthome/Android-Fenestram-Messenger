/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentAddUserBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick

class GroupGuestFragment :
    BaseFragment<GroupGuestState, GroupGuestEvent>(R.layout.fragment_add_user) {

    override val vm: GroupGuestViewModel by viewModel(
        getParamsInterface = GroupGuestContract.getParams
    )

    private val binding by fragmentViewBinding(FragmentAddUserBinding::bind)

    private val contactsAdapter = ContactsAdapter(onItemClicked = {
        vm.onContactClicked(it)
    }, onFooterClicked = { vm.onLinkFieldClick(it) })

    override fun renderState(state: GroupGuestState) {
        contactsAdapter.items = state.contacts.map { item ->
            when (item) {
                is AddContactViewItem.AddContact -> {
                    item.isSelected = state.addedContacts.find {
                        if (it is AddContactViewItem.AddContact)
                            it.userId == item.userId
                        else
                            false
                    } != null
                    item
                }
                else -> item
            }
        }
        contactsAdapter.notifyDataSetChanged()
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        binding.root.backgroundTintList = ColorStateList.valueOf(appTheme.bg1Color())
        binding.toolbar.setTextColor(appTheme.text0Color())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setOnButtonClickListener {
            vm.onBackClick()
        }

        addButton.onClick {
            vm.onAddClick()
        }
        listContacts.isNestedScrollingEnabled = false
        listContacts.adapter = contactsAdapter

    }

    override fun handleEvent(event: GroupGuestEvent) {
        when (event) {
            is GroupGuestEvent.CopyTextEvent -> {
                (requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("copy", event.link.replace("\\s".toRegex(), ""))
                )
                Toast.makeText(requireContext(), "Ссылка скопирована", Toast.LENGTH_SHORT).show()
            }
            is GroupGuestEvent.Loading -> {
                binding.addButton.loading(event.isLoading)
            }
        }
    }

    private fun FloatingActionButton.loading(isLoad: Boolean?) {
        if (isLoad == null) {
            return
        }
        binding.progress.isVisible = isLoad
        this.isEnabled = !isLoad
        if (isLoad) {
            this.setImageDrawable(null)
        } else {
            this.setImageResource(R.drawable.ic_add_button)
        }
    }

}