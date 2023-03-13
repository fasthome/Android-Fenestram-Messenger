/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.paging.ItemSnapshotList
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.core.ui.dialog.AcceptDialog
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentMessengerBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter.MessengerAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerItemTheme
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.AnimationUtil.getAlphaAnimation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.android.inject


class MessengerFragment :
    BaseFragment<MessengerState, MessengerEvent>(R.layout.fragment_messenger) {

    override val vm: MessengerViewModel by viewModel(
        getParamsInterface = MessengerNavigationContract.getParams
    )

    private val binding by fragmentViewBinding {
        FragmentMessengerBinding.bind(it)
    }

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

        etSearch.doOnTextChanged { text, start, before, count ->
            text?.let {
                vm.filterChats(text.toString())
                messageAdapter.refresh()
            }
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
            vm.backButtonClick()
            requireActivity().hideKeyboard(true)
        }

        messageAdapter.addOnPagesUpdatedListener {
            toggleEmptyView()

            if (lastScrollPosition == 0) binding.chatList.smoothScrollToPosition(0)
        }
        subscribeChats()

        fabActionListener = {
            vm.onCreateChatClicked()
        }

        ivChatSearch.onClick {
            vm.searchToolbarMode()
        }

        swipeRefresh.setOnRefreshListener {
            vm.fetchMessagesFromService()
            swipeRefresh.isRefreshing = false
        }
    }

    override fun syncTheme(appTheme: Theme) = with(binding) {
        appTheme.context = requireActivity().applicationContext
        appTheme.logoGradient(appNameHeader)
        bgGeometry.background = appTheme.backgroundGeometry()
        toolbar.setBackgroundColor(appTheme.bg3Color())
        toolbarTitle.setTextColor(appTheme.text0Color())
        etSearch.setTextColor(appTheme.text0Color())
        messageAdapter.snapshot().items.forEach {
            it.itemTheme = getMessengerItemTheme()
        }
        messageAdapter.notifyDataSetChanged()
    }

    private fun getMessengerItemTheme(): MessengerItemTheme {
        return MessengerItemTheme(
            nameColor = getTheme().text0Color(),
        )
    }

    override fun onResume() {
        super.onResume()
        vm.fetchNewMessages()
    }

    override fun renderState(state: MessengerState) {
        toggleToolbar(state.currentMode)
        if (state.currentMode == MessengerToolbarMode.Default) {
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
                theme = getTheme(),
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                event.id
            )
                .show()
            is MessengerEvent.CreateChatEvent -> CreateChatDialog.create(
                this,
                vm::createChatClicked,
                getTheme()
            ).show()
            is MessengerEvent.Invalidate -> {
                toggleEmptyView()
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

    private fun subscribeChats() {
        vm.fetchMessagesFromService()
        vm.fetchChats()
            .distinctUntilChanged()
            .collectWhenStarted(this@MessengerFragment) {
                val messengerViewItems = it.map { messengerItem ->
                    messengerItem.copy(
                        itemTheme = getMessengerItemTheme()
                    )
                }
                messageAdapter.submitData(messengerViewItems)
                messageAdapter.notifyItemChanged(0)
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    delay(5000)
                    val itemsAfter = messageAdapter.snapshot()
                    itemsAfter
                }
            }
    }

    override fun onBackPressed(): Boolean {
        return if (vm.canLeaveFragment())
            super.onBackPressed()
        else true
    }

    private fun toggleToolbar(currentToolbarMode: MessengerToolbarMode) {
        if(vm.currentToolbarState == currentToolbarMode) return
        vm.currentToolbarState = currentToolbarMode
        with(binding) {
            var constraintId = appNameHeader.id
            when (currentToolbarMode) {
                is MessengerToolbarMode.Select -> {
                    ivChatSearch.isVisible = false
                    appNameHeader.isVisible = false
                    constraintId = toolbar.id
                    toolbarTitle.isVisible = true
                    etSearch.isVisible = false
                    toolbar.isVisible = true
                    toolbarTitle.setText(currentToolbarMode.titleRes)
                }
                is MessengerToolbarMode.Default -> {
                    val hideAnim = getAlphaAnimation(0f, 1f)
                    ivChatSearch.startAnimation(hideAnim)
                    appNameHeader.startAnimation(hideAnim)
                    toolbar.startAnimation(getAlphaAnimation(1f, 0f))
                    toolbar.isVisible = false
                    etSearch.isVisible = false
                }
                is MessengerToolbarMode.Search -> {
                    toolbar.isVisible = true
                    toolbarTitle.isVisible = false
                    etSearch.isVisible = true
                    val hideAnim = getAlphaAnimation(1f, 0f)
                    ivChatSearch.startAnimation(hideAnim)
                    appNameHeader.startAnimation(hideAnim)
                    toolbar.startAnimation(getAlphaAnimation(0f, 1f))
                    constraintId = toolbar.id
                    etSearch.showKeyboard()
                }
            }

            val constraints = ConstraintSet().apply {
                clone(root)
                connect(
                    swipeRefresh.id,
                    ConstraintSet.TOP,
                    constraintId,
                    ConstraintSet.BOTTOM
                )
            }
            root.setConstraintSet(constraints)
        }
    }

    private fun toggleEmptyView() = with(binding) {
        emptyImage.isVisible = messageAdapter.itemCount == 0
        emptyText.isVisible = messageAdapter.itemCount == 0
    }

}
