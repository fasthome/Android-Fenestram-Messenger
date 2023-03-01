package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.content.res.ColorStateList
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderPermittedReactionBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderReactionsBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.PermittedReactionViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ReactionsViewItem
import io.fasthome.fenestram_messenger.util.*

class PermittedReactionsAdapter(onItemClicked: (PermittedReactionViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<PermittedReactionViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createPermittedReactionsAdapterDelegate(onItemClicked)
        )
    ) {}

fun createPermittedReactionsAdapterDelegate(
    onItemClicked: (PermittedReactionViewItem) -> Unit,
) =
    adapterDelegateViewBinding<PermittedReactionViewItem, HolderPermittedReactionBinding>(
        HolderPermittedReactionBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            reaction.text =
                HtmlCompat.fromHtml(item.permittedReaction, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

class ReactionsAdapter(onItemClicked: (ReactionsViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<ReactionsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createReactionsAdapterDelegate(onItemClicked)
        )
    ) {}

fun createReactionsAdapterDelegate(
    onItemClicked: (ReactionsViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ReactionsViewItem, HolderReactionsBinding>(
        HolderReactionsBinding::inflate,
    ) {
        binding.root.onClick {
            if (!item.setBySelf) context.vibrate()
            onItemClicked(item)
        }
        bindWithBinding {
            reaction.text = HtmlCompat.fromHtml(item.reaction, HtmlCompat.FROM_HTML_MODE_LEGACY)
            if (item.reactionBackground != null) {
                root.backgroundTintList = ColorStateList.valueOf(item.reactionBackground!!)
            }
            if (item.userCount > 3) {
                userCount.isVisible = true
                avatars.isVisible = false
                userCount.text = item.userCount.toString()
            } else {
                userCount.isVisible = false
                avatars.isVisible = true
                val avatarImageViews = listOf(avatar3, avatar2, avatar1)
                avatarImageViews.forEachIndexed { ind, iv ->
                    item.avatars.getOrNull(ind)?.let {
                        iv.isVisible = true
                        iv.loadCircle(it)
                    } ?: run { iv.isVisible = false }
                }
            }
        }
    }