/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize

interface AuthFeature {

    val authNavigationContract: NavigationContractApi<NoParams, AuthResult>

    suspend fun isUserAuthorized(): CallResult<Boolean>

    sealed class AuthResult : Parcelable {

        @Parcelize
        object Success : AuthResult()

        @Parcelize
        object Canceled : AuthResult()
    }
}