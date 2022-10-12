package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.app.Dialog
import android.os.Bundle
import android.view.View
import io.fasthome.component.person_detail.PersonDetailDialog
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentGroupParticipantsBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter.ParticipantsAdapter
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.supportBottomSheetScroll

class GroupParticipantsFragment :
    BaseFragment<GroupParticipantsState, GroupParticipantsEvent>(R.layout.fragment_group_participants),
    InterfaceFragment<GroupParticipantsInterface> {

    override val vm: GroupParticipantsViewModel by viewModel(
        getParamsInterface = GroupParticipantsComponentContract.getParams
    )

    private val adapter = ParticipantsAdapter(
        onMenuClicked = { id -> vm.onMenuClicked(id) },
        onAnotherUserClicked = { userId -> vm.onAnotherUserClicked(userId) }
    )

    private val binding by fragmentViewBinding(FragmentGroupParticipantsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.participants.adapter = adapter
        binding.participants.supportBottomSheetScroll()

        binding.addUserToChat.onClick {
            vm.onAddUserToChat()
        }
        
        latestPersonDetailDialog = Dialog(requireContext())
    }

    override fun renderState(state: GroupParticipantsState) {
        adapter.items = state.participants
    }

    lateinit var latestPersonDetailDialog: Dialog

    override fun handleEvent(event: GroupParticipantsEvent) {
        when (event) {
            is GroupParticipantsEvent.MenuOpenEvent -> {
                UserMenuDialog.create(
                    fragment = this,
                    delete = vm::onDeleteUserClicked,
                    addToContacts = vm::onAddToContactsClicked,
                    id = event.id,
                    name = event.name,
                    phone = event.phone,
                ).show()
            }
            is GroupParticipantsEvent.ShowPersonDetailDialog -> {
                if (!latestPersonDetailDialog.isShowing) {
                    latestPersonDetailDialog = PersonDetailDialog
                        .create(
                            fragment = this,
                            personDetail = event.selectedPerson,
                            launchFaceCallClicked = {
                                //TODO
                            },
                            launchCallClicked = {
                                //TODO
                            },
                            launchConversationClicked = {
                                vm.onLaunchConversationClicked(it)
                            },
                            onAvatarClicked = { avatarUrl ->
                                vm.onImageClicked(url = avatarUrl)
                            })
                    latestPersonDetailDialog.show()
                }
            }
        }
    }

    override fun getInterface(): GroupParticipantsInterface = vm

}