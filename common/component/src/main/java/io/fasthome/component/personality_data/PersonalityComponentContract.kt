/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import android.os.Parcelable
import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContract
import kotlinx.android.parcel.Parcelize

val PersonalityComponentContract =
    ComponentFragmentContract<PersonalityInterface, PersonalityParams, PersonalityComponentFragment>()


@Parcelize
class PersonalityParams(
    val userDetail: UserDetail?,
    val nameVisible : Boolean,
    val visibilityIcons : Boolean
) : Parcelable