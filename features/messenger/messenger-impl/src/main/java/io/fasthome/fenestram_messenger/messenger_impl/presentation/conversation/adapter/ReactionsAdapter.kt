package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.text.Html
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderReactionBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ReactionViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.onClick

class ReactionsAdapter(onItemClicked: (ReactionViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<ReactionViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createReactionsAdapterDelegate(onItemClicked)
        )
    ) {}

fun createReactionsAdapterDelegate(
    onItemClicked: (ReactionViewItem) -> Unit
) =
    adapterDelegateViewBinding<ReactionViewItem, HolderReactionBinding>(
        HolderReactionBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            reaction.text = Html.fromHtml(item.reaction, Html.FROM_HTML_MODE_LEGACY)
        }
    }