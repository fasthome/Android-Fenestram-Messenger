/**
 * Created by Dmitry Popov on 11.08.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProfileImageResponse(

    @SerialName("filename")
    val filename: String,

    @SerialName("pathToFile")
    val pathToFile: String

)