package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.*

class MessengerAdapter(onChatClicked: (MessengerViewItem) -> Unit, onProfileClicked: (MessengerViewItem) -> Unit) :
    PagerDelegateAdapter<MessengerViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            keyExtractor = MessengerViewItem::lastMessage
        ),
        delegates = listOf(
            createMessengerAdapter(onChatClicked, onProfileClicked)
        )
    )

fun createMessengerAdapter(chatClicked: (MessengerViewItem) -> Unit, onProfileClicked: (MessengerViewItem) -> Unit) =
    createAdapterDelegate<MessengerViewItem, MessangerChatItemBinding>(
        inflate = MessangerChatItemBinding::inflate,
        bind = { item, binding ->
            with(binding) {
                root.onClick {
                    chatClicked(item)
                }
                profilePicture.onClick {
                    onProfileClicked(item)
                }
                nameView.setPrintableText(item.name)
                lastMessage.setPrintableText(item.lastMessage)
                timeView.setPrintableText(item.time)
                profilePicture.loadCircle(
                    url = item.profileImageUrl,
                    placeholderRes = R.drawable.ic_baseline_account_circle_24
                )
            }
        }
    )
