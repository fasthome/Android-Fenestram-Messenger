package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Parcelable
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import kotlinx.android.parcel.Parcelize

object PersonalityNavigationContract : NavigationContract<PersonalityNavigationContract.Params, AuthFeature.AuthResult>(PersonalityFragment::class) {

    @Parcelize
    class Params(
        val auth : Boolean = true,
        val isEdit : Boolean = false,
        val userDetail: UserDetail
    ) : Parcelable

}