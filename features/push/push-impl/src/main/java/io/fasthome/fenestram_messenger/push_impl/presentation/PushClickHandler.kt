package io.fasthome.fenestram_messenger.push_impl.presentation

import android.content.Intent
import io.fasthome.fenestram_messenger.main_api.DeepLinkResult
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.push_impl.data.service.mapper.RemoteMessagesMapper
import io.fasthome.fenestram_messenger.push_impl.domain.entity.PushClickData

class PushClickHandler(
    private val remoteMessagesMapper: RemoteMessagesMapper,
    private val messengerFeature : MessengerFeature
) {

    fun handle(intent: Intent): DeepLinkResult? {
        return when(val pushData = remoteMessagesMapper.getPushClickData(intent)){
            is PushClickData.Unknown -> {
                null
            }
            is PushClickData.Chat -> {
                DeepLinkResult.NewChain(
                    messengerFeature.conversationNavigationContract.createParams(
                        MessengerFeature.Params(
                            chatId = pushData.chatId,
                            userIds = listOf(),
                            chatName = pushData.chatName ?: return null,
                            isGroup = pushData.isGroup ?: return null
                        )
                    ),
                )
            }
        }
    }
}