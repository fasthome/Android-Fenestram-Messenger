package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemCurrentUserBinding
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemParticipantBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.AnotherUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.CurrentUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.*

class ParticipantsAdapter(onMenuClicked: (Long) -> Unit) :
    AsyncListDifferDelegationAdapter<ParticipantsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createParticipantsAdapter(onMenuClicked),
            createCurrentUserAdapterDelegate()
        )
    )

fun createCurrentUserAdapterDelegate() =
    adapterDelegateViewBinding<CurrentUserViewItem, ItemCurrentUserBinding>(
        ItemCurrentUserBinding::inflate,
    ) {
        bindWithBinding {
            avatar.loadCircle(item.avatar)
            name.setPrintableText(item.name)
        }
    }


fun createParticipantsAdapter(onMenuClicked: (Long) -> Unit) =
    adapterDelegateViewBinding<AnotherUserViewItem, ItemParticipantBinding>(
        ItemParticipantBinding::inflate
    ) {
        binding.dropdownMenu.setOnClickListener {
            onMenuClicked(item.userId)
        }

        bindWithBinding {
            avatar.loadCircle(item.avatar)
            name.setPrintableText(item.name)
        }
    }