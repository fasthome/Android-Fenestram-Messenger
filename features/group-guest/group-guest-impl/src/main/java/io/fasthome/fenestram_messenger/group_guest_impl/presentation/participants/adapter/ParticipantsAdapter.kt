package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadAvatarWithGradient
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemCurrentUserBinding
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemParticipantBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.AnotherUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.CurrentUserViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.*

class ParticipantsAdapter(
    onMenuClicked: (Long) -> Unit,
    onAnotherUserClicked: (Long) -> Unit
) :
    AsyncListDifferDelegationAdapter<ParticipantsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createParticipantsAdapter(onMenuClicked, onAnotherUserClicked),
            createCurrentUserAdapterDelegate()
        )
    )

fun createCurrentUserAdapterDelegate() =
    adapterDelegateViewBinding<CurrentUserViewItem, ItemCurrentUserBinding>(
        ItemCurrentUserBinding::inflate,
    ) {
        bindWithBinding {
            item.textColor?.let { it1 -> name.setTextColor(it1) }
            avatar.loadAvatarWithGradient(item.avatar, username = item.originalName)
            name.setPrintableText(item.name)
        }
    }


fun createParticipantsAdapter(
    onMenuClicked: (Long) -> Unit,
    onAnotherUserClicked: (Long) -> Unit
) =
    adapterDelegateViewBinding<AnotherUserViewItem, ItemParticipantBinding>(
        ItemParticipantBinding::inflate
    ) {
        binding.dropdownMenu.setOnClickListener {
            onMenuClicked(item.userId)
        }
        binding.root.setOnClickListener { onAnotherUserClicked(item.userId) }

        bindWithBinding {
            item.textColor?.let { it1 -> name.setTextColor(it1) }
            avatar.loadAvatarWithGradient(item.avatar, username = context.getPrintableText(item.name))
            name.setPrintableText(item.name)
        }
    }