/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.ui.dialog.DeleteChatDialog
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentMessengerBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter.MessengerAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter.MessengerItemTouchHelper
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.collectLatestWhenStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.android.inject

class MessengerFragment :
    BaseFragment<MessengerState, MessengerEvent>(R.layout.fragment_messenger) {

    override val vm: MessengerViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentMessengerBinding::bind)
    
    private val environment by inject<Environment>()

    private var messageAdapter = MessengerAdapter(environment = environment,
        onChatClicked = {
            vm.launchConversation(it)
        },
        onProfileClicked = {
            vm.onProfileClicked(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chatList.adapter = messageAdapter

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        ItemTouchHelper(
            MessengerItemTouchHelper(
                adapter = messageAdapter,
                deleteChat = vm::onChatDelete
            )
        ).attachToRecyclerView(binding.chatList)

        binding.chatsSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        }

        vm.items
            .distinctUntilChanged()
            .collectLatestWhenStarted(this) {
                messageAdapter.submitData(it)
            }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchNewMessages()
    }

    override fun renderState(state: MessengerState) = nothingToRender()

    override fun handleEvent(event: MessengerEvent) {
        when (event) {
            is MessengerEvent.DeleteChatEvent -> DeleteChatDialog.create(
                this,
                PrintableText.StringResource(R.string.common_delete_chat_dialog),
                vm::deleteChat,
                event.id
            )
                .show()
        }
    }

    override fun onFabClicked(): Boolean {
        vm.onCreateChatClicked()
        return super.onFabClicked()
    }

    override fun onStop() {
        super.onStop()
        vm.unsubscribeMessages()
    }

}
