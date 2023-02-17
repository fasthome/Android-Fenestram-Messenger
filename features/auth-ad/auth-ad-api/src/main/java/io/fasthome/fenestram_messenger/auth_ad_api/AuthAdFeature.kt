/**
 * Created by Vladimir Rudakov on 16.02.2023.
 */
package io.fasthome.fenestram_messenger.auth_ad_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import kotlinx.parcelize.Parcelize

interface AuthAdFeature {

    val authAdNavigationContract: NavigationContractApi<NoParams, AuthAdResult>

    sealed class AuthAdResult : Parcelable {

        @Parcelize
        object Success : AuthAdResult()

        @Parcelize
        object Canceled : AuthAdResult()
    }
}