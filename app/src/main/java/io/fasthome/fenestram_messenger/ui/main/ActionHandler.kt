/**
 * Created by Dmitry Popov on 25.01.2023.
 */
package io.fasthome.fenestram_messenger.ui.main

import android.content.Intent
import io.fasthome.fenestram_messenger.main_api.DeepLinkResult
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_api.entity.ActionMessageBlank


class ActionHandler(
    private val messengerFeature: MessengerFeature
) {

    fun handle(intent: Intent): DeepLinkResult? = when (intent.type) {
        "text/plain" -> {
            val extraText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (extraText != null) {
                DeepLinkResult.NewChain(
                    messengerFeature.messengerNavigationContract.createParams(
                        MessengerFeature.MessengerParams(
                            chatSelectionMode = true,
                            forwardMessage = null,
                            newMessage = ActionMessageBlank.Text(extraText)
                        )
                    ),
                )
            } else {
                null
            }
        }
        "image/jpeg",
        "image/png" -> {
            val items = intent.clipData
            if (items?.itemCount != null && items.itemCount > 0) {
                val extraImage = items.getItemAt(0)

                if (extraImage != null) {
                    DeepLinkResult.NewChain(
                        messengerFeature.messengerNavigationContract.createParams(
                            MessengerFeature.MessengerParams(
                                chatSelectionMode = true,
                                forwardMessage = null,
                                newMessage = ActionMessageBlank.Image(
                                    extraImage.uri
                                )
                            )
                        )
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }
        else -> {
            null
        }
    }
}