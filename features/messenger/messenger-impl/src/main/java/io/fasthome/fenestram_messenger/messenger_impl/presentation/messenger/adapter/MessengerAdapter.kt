package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.ui.extensions.loadAvatarWithGradient
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.*

class MessengerAdapter(
    profileImageUrlConverter: StorageUrlConverter,
    onChatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit,
    onDeleteChat: (id: Long) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    handler: Handler,
) :
    PagerDelegateAdapter<MessengerViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            keyExtractor = MessengerViewItem::lastMessage
        ),
        delegates = listOf(
            createMessengerAdapter(
                profileImageUrlConverter,
                onChatClicked,
                onProfileClicked,
                onDeleteChat,
                viewBinderHelper,
                handler
            )
        )
    )

@SuppressLint("ClickableViewAccessibility")
fun createMessengerAdapter(
    profileImageUrlConverter: StorageUrlConverter,
    chatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit,
    onDeleteChat: (id: Long) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    handler: Handler
) =
    createAdapterDelegate<MessengerViewItem, MessangerChatItemBinding>(
        inflate = MessangerChatItemBinding::inflate,
        bind = { item, binding ->
            handler.removeCallbacksAndMessages(null)
            with(binding) {
                viewBinderHelper.bind(root, item.id.toString())
                viewBinderHelper.setOpenOnlyOne(true)

                itemChatLayout.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        viewBinderHelper.closeOpened(item.id.toString(), root)
                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }

                itemChatLayout.onClick {
                    if (viewBinderHelper.openCount == 0)
                        chatClicked(item)
                    else
                        viewBinderHelper.closeAll()
                }
                profilePicture.onClick {
                    if (viewBinderHelper.openCount == 0)
                        onProfileClicked(item)
                    else
                        viewBinderHelper.closeAll()
                }

                deleteLayout.onClick {
                    onDeleteChat(item.id)
                    root.close(true)
                }

                nameView.setPrintableText(item.name)
                groupPicture.isVisible = item.isGroup
                when (item.lastMessage) {
                    is LastMessage.Image -> {
                        image.loadRounded(profileImageUrlConverter.convert(item.lastMessage.imageUrl.firstOrNull()?.url))
                        if(item.lastMessage.imageUrl.isNotEmpty()) {
                            lastMessage.text = image.context.resources.getQuantityString(R.plurals.image_quantity, item.lastMessage.imageUrl.size,item.lastMessage.imageUrl.size)
                        } else {
                            lastMessage.setText(R.string.messenger_image)
                        }
                        image.isVisible = true
                        statusDots.setPrintableText(PrintableText.EMPTY)
                    }
                    is LastMessage.Text -> {
                        lastMessage.setPrintableText(item.lastMessage.text)
                        lastMessage.setTextColor(
                            ContextCompat.getColor(
                                lastMessage.context,
                                R.color.gray2
                            )
                        )
                        image.isVisible = false
                        statusDots.setPrintableText(PrintableText.EMPTY)
                    }
                    is LastMessage.UserStatus -> {
                        val runnable = object : Runnable {
                            override fun run() {
                                val newDotCount = statusDots.text.count() % 3 + 1
                                statusDots.text = ".".repeat(newDotCount)
                                handler.postDelayed(this, 300)
                            }
                        }

                        lastMessage.setPrintableText(item.lastMessage.status)
                        lastMessage.setTextColor(
                            ContextCompat.getColor(
                                lastMessage.context,
                                R.color.main_active
                            )
                        )
                        statusDots.setPrintableText(PrintableText.Raw("."))
                        handler.postDelayed(runnable, 300)
                        image.isVisible = false
                    }
                    is LastMessage.Document -> {
                        lastMessage.setText(R.string.messenger_document)
                        image.isVisible = true
                        image.setImageResource(R.drawable.ic_document)
                        statusDots.setPrintableText(PrintableText.EMPTY)
                    }
                }
                timeView.setPrintableText(item.time)
                profilePicture.loadAvatarWithGradient(
                    url = item.profileImageUrl,
                    username = root.context.getPrintableText(item.name)
                )
                status.setImageResource(item.statusIcon)
                pendingAmount.setPrintableText(item.pendingAmount)

                // Update theme
                item.itemTheme?.let {
                    nameView.setTextColor(it.nameColor)
                }
            }
        }
    )