package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
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
                when(item.lastMessage){
                    is LastMessage.Image -> {
                        lastMessage.setText(R.string.messenger_image)
                        image.loadRounded(item.lastMessage.imageUrl)
                        image.isVisible = true
                    }
                    is LastMessage.Text -> {
                        lastMessage.setPrintableText(item.lastMessage.text)
                        image.isVisible = false
                    }
                }
                timeView.setPrintableText(item.time)
                profilePicture.loadCircle(
                    url = item.profileImageUrl,
                    placeholderRes = R.drawable.ic_baseline_account_circle_24
                )
            }
        }
    )

class MessengerItemTouchHelper(
    val adapter: MessengerAdapter,
    val deleteChat: (id: Long) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> {
                deleteChat(adapter.snapshot().items[viewHolder.bindingAdapterPosition].id)
                adapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val p = Paint()
        p.color = ContextCompat.getColor(recyclerView.context, R.color.red)

        if (dX > 0) {
            c.drawRect(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                dX,
                itemView.bottom.toFloat(),
                p
            )
        } else {

            c.drawRect(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                p
            )

        }

        if (dX.equals(0f) && !isCurrentlyActive) {
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                p
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete)
            ?.let { deleteDrawable ->
                val itemHeight: Int = itemView.height
                val intrinsicHeight = deleteDrawable.intrinsicHeight
                val intrinsicWidth = deleteDrawable.intrinsicWidth

                val deleteIconTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin: Int = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft: Int = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom: Int = deleteIconTop + intrinsicHeight

                deleteDrawable.setBounds(
                    deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom
                )
                deleteDrawable.draw(c)
            }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(
        c: Canvas,
        left: Float?,
        top: Float?,
        right: Float?,
        bottom: Float?,
        p: Paint
    ) {
        c.drawRect(left!!, top!!, right!!, bottom!!, p)
    }
}
