package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Parcelable
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UserDetail
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlinx.android.parcel.Parcelize

object PersonalityNavigationContract : NavigationContract<PersonalityNavigationContract.Params, AuthFeature.AuthResult>(PersonalityFragment::class) {

    @Parcelize
    class Params(
        val auth : Boolean = true,
        val userDetail: UserDetail
    ) : Parcelable

}