package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

class MessengerAdapter(
    environment: Environment,
    onChatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit
) :
    PagerDelegateAdapter<MessengerViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            keyExtractor = MessengerViewItem::lastMessage
        ),
        delegates = listOf(
            createMessengerAdapter(environment, onChatClicked, onProfileClicked)
        )
    )

fun createMessengerAdapter(
    environment: Environment,
    chatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit
) =
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
                groupPicture.isVisible = item.isGroup
                when (item.lastMessage) {
                    is LastMessage.Image -> {
                        lastMessage.setText(R.string.messenger_image)
                        image.loadRounded(environment.endpoints.apiBaseUrl.dropLast(1) + item.lastMessage.imageUrl)
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
    recyclerView: RecyclerView,
    private val underlayButton: UnderlayButton,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var swipedPosition = -1

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        if (swipedPosition < 0) return@OnTouchListener false
        underlayButton.handle(event, swipedPosition)
        underlayButton.adapter.notifyItemChanged(swipedPosition)
        swipedPosition = -1
        true
    }

    init {
        recyclerView.setOnTouchListener(touchListener)
    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (swipedPosition != position)
            underlayButton.adapter.notifyItemChanged(swipedPosition)
        swipedPosition = position
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
        val maxDX = (-underlayButton.intrinsicWidth).coerceAtLeast(dX)
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Log.d("here", dX.toString())
            if (dX < 0)
                underlayButton.draw(
                    c,
                    RectF(
                        itemView.right - kotlin.math.abs(maxDX),
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                )
        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            maxDX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

}

class UnderlayButton(
    val adapter: MessengerAdapter,
    private val context: Context,
    private val onClickEvent: (id: Long) -> Unit
) {
    private var clickableRegion: RectF? = null
    val intrinsicWidth: Float = 80.dp.toFloat()

    fun draw(canvas: Canvas, rect: RectF) {
        val paint = Paint()

        paint.color = ContextCompat.getColor(context, R.color.red)
        canvas.drawRect(rect, paint)

        ContextCompat.getDrawable(context, R.drawable.ic_delete)
            ?.let { deleteDrawable ->
                val itemHeight = rect.height()
                val intrinsicHeight = deleteDrawable.intrinsicHeight
                val intrinsicWidth = deleteDrawable.intrinsicWidth

                val deleteIconTop = rect.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = rect.left + deleteIconMargin
                val deleteIconRight = rect.left + deleteIconMargin + intrinsicWidth
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                deleteDrawable.setBounds(
                    deleteIconLeft.toInt(),
                    deleteIconTop.toInt(),
                    deleteIconRight.toInt(),
                    deleteIconBottom.toInt()
                )

                deleteDrawable.draw(canvas)
            }
        clickableRegion = rect
    }

    fun handle(event: MotionEvent, position: Int) {
        clickableRegion?.let {
            if (it.contains(event.x, event.y)) {
                onClickEvent.invoke(adapter.snapshot().items[position].id)
            }
        }
    }
}
