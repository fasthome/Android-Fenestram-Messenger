/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow

interface PersonalityInterface {
    val fieldStateChanges: Flow<FillState>

    fun getFields() : UserDetail
    fun setFields(userDetail: UserDetail)

    fun runEdit(edit : Boolean)
}

@Parcelize
class UserDetail(
    val name : String,
    val mail : String,
    val birthday : String,
    val nickname : String
) : Parcelable

enum class FillState { Empty, Filled, Error }