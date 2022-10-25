package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText

class MessengerAdapter(
    environment: Environment,
    onChatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit,
    onDeleteChat: (id: Long) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    handler: Handler
) :
    PagerDelegateAdapter<MessengerViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            keyExtractor = MessengerViewItem::lastMessage
        ),
        delegates = listOf(
            createMessengerAdapter(
                environment,
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
    environment: Environment,
    chatClicked: (MessengerViewItem) -> Unit,
    onProfileClicked: (MessengerViewItem) -> Unit,
    onDeleteChat: (id: Long) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    handler: Handler
) =
    createAdapterDelegate<MessengerViewItem, MessangerChatItemBinding>(
        inflate = MessangerChatItemBinding::inflate,
        bind = { item, binding ->
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
                        lastMessage.setText(R.string.messenger_image)
                        image.loadRounded(environment.endpoints.baseUrl.dropLast(1) + item.lastMessage.imageUrl)
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
                        handler.removeCallbacksAndMessages(null)
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
                                R.color.blue
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
                profilePicture.loadCircle(
                    url = item.profileImageUrl,
                    placeholderRes = R.drawable.ic_baseline_account_circle_24
                )
                status.setImageResource(item.statusIcon)
                pendingAmount.setPrintableText(item.pendingAmount)
            }
        }
    )