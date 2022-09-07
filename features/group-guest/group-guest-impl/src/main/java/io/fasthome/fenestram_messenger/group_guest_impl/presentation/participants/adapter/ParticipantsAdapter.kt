package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.ItemParticipantBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.model.ParticipantsViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.setPrintableText

class ParticipantsAdapter : AsyncListDifferDelegationAdapter<ParticipantsViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createParticipantsAdapter()
    )
)

fun createParticipantsAdapter() = adapterDelegateViewBinding<ParticipantsViewItem, ItemParticipantBinding>(
    ItemParticipantBinding::inflate
) {

    bindWithBinding {
        avatar.loadCircle(item.avatar)
        name.setPrintableText(item.name)
    }
}
