/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.parcelize.Parcelize

interface MessengerFeature {

    val messengerNavigationContract: NavigationContractApi<NoParams, NoResult>

    val conversationNavigationContract: NavigationContractApi<Params, NoResult>

    @Parcelize
    data class Params(
        val chatId: String? = null,
        val userIds: List<Long>,
        val chatName: String,
        val isGroup: Boolean
    ) : Parcelable

}