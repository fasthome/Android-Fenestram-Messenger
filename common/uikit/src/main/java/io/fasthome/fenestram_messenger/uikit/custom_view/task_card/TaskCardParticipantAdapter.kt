package io.fasthome.fenestram_messenger.uikit.custom_view.task_card

import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.uikit.custom_view.emptyAvatarWithUsername
import io.fasthome.fenestram_messenger.uikit.databinding.TaskCardParticipantViewItemBinding
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class TaskCardParticipantAdapter : AsyncListDifferDelegationAdapter<ConfeeTaskCardState.UserViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createTaskParticipantAdapterDelegate(),
    )
)


fun createTaskParticipantAdapterDelegate() =
    adapterDelegateViewBinding<ConfeeTaskCardState.UserViewItem, TaskCardParticipantViewItemBinding>(
        TaskCardParticipantViewItemBinding::inflate,
    ) {
        bindWithBinding {
            ivParticipantAvatar.loadAvatarWithGradient(item)
        }
    }

fun ImageView.loadAvatarWithGradient(userViewItem: ConfeeTaskCardState.UserViewItem) {
    if (userViewItem.avatar.isEmpty()) {
        Glide.with(this)
            .load(emptyAvatarWithUsername(userViewItem.name))
            .into(this)
    } else {
        Glide
            .with(this)
            .load(userViewItem.avatar)
            .placeholder(emptyAvatarWithUsername(userViewItem.name)?.toDrawable(context.resources))
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(this)
    }
}