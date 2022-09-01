/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Contact(
    val id: Long,
    val phone: String,
    val userName: String?,
    val userId: Long?,
    val user : User? = null
) : Parcelable

@Parcelize
data class User(
    val id : Long,
    val phone : String,
    val name : String,
    val nickname : String,
    val email : String,
    val birth : String,
    val avatar : String,
    val isOnline : Boolean,
    val lastActive : ZonedDateTime
) : Parcelable {
    constructor(id : Long) : this(id, "", "", "", "", "", "", true, ZonedDateTime.now())
}