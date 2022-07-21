package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.os.Parcelable
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import kotlinx.parcelize.Parcelize

object CodeNavigationContract : NavigationContract<CodeNavigationContract.Params, AuthFeature.AuthResult>(CodeFragment::class) {
    @Parcelize
    data class Params(val phoneNumber: String) : Parcelable
}