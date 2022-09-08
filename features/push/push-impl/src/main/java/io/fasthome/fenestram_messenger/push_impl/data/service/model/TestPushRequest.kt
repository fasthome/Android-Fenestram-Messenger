/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestPushRequest(

    @SerialName("text")
    val text : String,

    @SerialName("message_type")
    val messageType : String,

)