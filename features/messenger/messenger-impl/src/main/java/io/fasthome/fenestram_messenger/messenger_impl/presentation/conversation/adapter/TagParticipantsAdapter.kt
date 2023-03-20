package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ItemUserTagBinding
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.onClick

class TagParticipantsAdapter(
    onUserClicked: (User) -> Unit,
    nameThemeColor: Int
) :
    AsyncListDifferDelegationAdapter<User>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createTagUserAdapterDelegate(onUserClicked,nameThemeColor)
        )
    )

fun createTagUserAdapterDelegate(
    onUserClicked: (User) -> Unit,
    nameThemeColor: Int
) =
    adapterDelegateViewBinding<User, ItemUserTagBinding>(
        ItemUserTagBinding::inflate,
    ) {
        bindWithBinding {
            root.onClick {
                onUserClicked(item)
            }
            avatar.loadCircle(item.avatar)
            name.text = item.name
            name.setTextColor(nameThemeColor)
            nickname.text = "@${item.nickname}"
        }
    }