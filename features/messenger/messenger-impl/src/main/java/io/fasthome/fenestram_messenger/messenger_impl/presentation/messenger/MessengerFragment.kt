/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentMessengerBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter.MessengerAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.collectLatestWhenStarted
import io.fasthome.fenestram_messenger.util.onClick
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.android.inject

class MessengerFragment :
    BaseFragment<MessengerState, MessengerEvent>(R.layout.fragment_messenger) {

    override val vm: MessengerViewModel by viewModel(
        getParamsInterface = MessengerNavigationContract.getParams
    )

    private val binding by fragmentViewBinding(FragmentMessengerBinding::bind)

    private val profileImageUrlConverter by inject<StorageUrlConverter>()

    private val viewBinderHelper = ViewBinderHelper()

    private var lastScrollPosition = 0

    private lateinit var fabActionListener: () -> Unit

    private var messageAdapter = MessengerAdapter(
        profileImageUrlConverter = profileImageUrlConverter,
        onChatClicked = {
            vm.launchConversation(it)
        },
        onProfileClicked = {
            vm.onProfileClicked(it)
        },
        onDeleteChat = { vm.onChatDelete(it) },
        viewBinderHelper = viewBinderHelper,
        handler = Handler(Looper.getMainLooper())
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        chatList.adapter = messageAdapter
        chatList.itemAnimator = null

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        chatsSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    vm.filterChats(it)
                    messageAdapter.refresh()
                }
                return true
            }
        })

        messageAdapter.addOnPagesUpdatedListener {
            binding.llEmptyView.isVisible = messageAdapter.itemCount < 1
            if (lastScrollPosition == 0) binding.chatList.smoothScrollToPosition(0)
        }
        val linearLayoutManager =
            LinearLayoutManager(requireContext())
        chatList.layoutManager = linearLayoutManager

        chatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastScrollPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (lastScrollPosition == 0) {
                    vm.onReadMessages()
                }
                if (dy > 0) {
                    vm.onScrolledDown(true)
                } else if (dy < 0) {
                    vm.onScrolledDown(false)
                }
            }
        })

        backButton.onClick {
            vm.exitToConversation()
        }

        vm.items
            .distinctUntilChanged()
            .collectLatestWhenStarted(this@MessengerFragment) {
                messageAdapter.submitData(it)
            }

        fabActionListener = {
            vm.onCreateChatClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchNewMessages()
    }

    override fun renderState(state: MessengerState) {
        if (state.isSelectMode) {
            toggleToolbar(true, R.string.forward)
        } else {
            toggleToolbar(false)
            when {
                state.scrolledDown || state.newMessagesCount != 0 && lastScrollPosition != 0 -> {
                    updateFabIcon(iconRes = R.drawable.ic_arrow_up, state.newMessagesCount)
                    fabActionListener = {
                        binding.chatList.smoothScrollToPosition(0)
                    }
                }
                state.newMessagesCount == 0 && !state.scrolledDown -> {
                    fabActionListener = {
                        vm.onCreateChatClicked()
                    }
                    updateFabIcon(iconRes = null, badgeCount = state.newMessagesCount)
                }
            }
        }
    }

    override fun handleEvent(event: MessengerEvent) {
        when (event) {
            is MessengerEvent.DeleteChatEvent -> AcceptDialog.create(
                this,
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                event.id
            )
                .show()
            is MessengerEvent.CreateChatEvent -> CreateChatDialog.create(
                this,
                vm::createChatClicked
            ).show()
            is MessengerEvent.ProgressEvent -> {
                binding.chatLoadProgress.alpha = if(event.isProgress) 1f else 0f
            }
        }
    }

    override fun onFabClicked(): Boolean {
        fabActionListener.invoke()
        return super.onFabClicked()
    }

    override fun onPause() {
        super.onPause()
        viewBinderHelper.closeAll()
    }

    override fun onStop() {
        super.onStop()
        vm.unsubscribeMessages()
    }

    private fun toggleToolbar(visible: Boolean, @StringRes text: Int? = null) {
        with(binding) {
            toolbar.isVisible = visible
            text?.let { toolbarTitle.setText(it) }
            appNameHeader.isVisible = !visible
            val constraints = ConstraintSet().apply {
                clone(root)
                connect(
                    chatsSv.id,
                    ConstraintSet.TOP,
                    if (visible) toolbar.id else appNameHeader.id,
                    ConstraintSet.BOTTOM
                )
            }
            root.setConstraintSet(constraints)
        }
    }

}
