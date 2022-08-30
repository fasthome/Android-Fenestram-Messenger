/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class CreateInfoViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: CreateInfoContract.Params
) : BaseViewModel<CreateInfoState, CreateInfoEvent>(router, requestParams) {

    private val conversationLauncher = registerScreen(ConversationNavigationContract)

    override fun createInitialState(): CreateInfoState {
        return CreateInfoState(params.contacts.map(::mapToContactViewItem))
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun onReadyClicked(chatName: String) {
        router.backTo(null)
        conversationLauncher.launch(
            ConversationNavigationContract.Params(
                Chat(
                    null,
                    name = chatName,
                    users = params.contacts.map { User(id = it.userId) },
                    messages = listOf(),
                    time = null,
                    avatar = null
                )
            )
        )
    }

}