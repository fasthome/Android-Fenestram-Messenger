/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize

interface ProfileFeature {
    val profileNavigationContract : NavigationContractApi<NoParams, ProfileResult>

    suspend fun isUserAuthorized(): CallResult<Boolean>

    sealed class ProfileResult : Parcelable {

        @Parcelize
        object Success : ProfileResult()

        @Parcelize
        object Canceled : ProfileResult()
    }
}
