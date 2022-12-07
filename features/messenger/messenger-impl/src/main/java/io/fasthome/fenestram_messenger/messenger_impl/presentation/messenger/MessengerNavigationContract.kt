/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.os.Parcelable
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

object MessengerNavigationContract :
    NavigationContract<MessengerNavigationContract.Params, NoResult>(
        MessengerFragment::class) {
    @Parcelize
    data class Params(
        val chatSelectionMode: Boolean = false,
        val forwardMessage: MessengerFeature.ForwardMessage? = null
    ) : Parcelable

}