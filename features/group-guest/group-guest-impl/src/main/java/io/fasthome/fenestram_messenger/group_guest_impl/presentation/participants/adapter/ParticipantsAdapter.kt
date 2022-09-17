package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemParticipantBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.*

class ParticipantsAdapter(onMenuClicked: (Long, View) -> Unit) :
    AsyncListDifferDelegationAdapter<ParticipantsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createParticipantsAdapter(onMenuClicked)
        )
    )

fun createParticipantsAdapter(onMenuClicked: (Long, View) -> Unit) =
    adapterDelegateViewBinding<ParticipantsViewItem, ItemParticipantBinding>(
        ItemParticipantBinding::inflate
    ) {
        binding.dropdownMenu.setOnClickListener {
            onMenuClicked(item.userId, it)
        }

        bindWithBinding {
            avatar.loadCircle(item.avatar)
            name.setPrintableText(item.name)
        }
    }
