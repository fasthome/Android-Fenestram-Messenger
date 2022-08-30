/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl

import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.MessengerNavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.mapParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

class MessengerFeatureImpl : MessengerFeature {

    override val messengerNavigationContract = MessengerNavigationContract

    override val conversationNavigationContract: NavigationContractApi<MessengerFeature.Params, NoResult> =
        ConversationNavigationContract.mapParams {
            ConversationNavigationContract.Params(
                chat = Chat(id = null, users = listOf(User(id = it.userId)), messages = listOf(), time = null, name = it.userName, avatar = null)
            )
        }

}