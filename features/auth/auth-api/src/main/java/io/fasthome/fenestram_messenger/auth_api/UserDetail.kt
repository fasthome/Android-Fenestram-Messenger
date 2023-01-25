/**
 * Created by Dmitry Popov on 08.08.2022.
 */
package io.fasthome.fenestram_messenger.auth_api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetail(
    val id: Long,
    val phone: String,
    val name: String,
    val email: String,
    val nickname: String,
    val birth: String,
    val profileImageUrl: String?
) : Parcelable