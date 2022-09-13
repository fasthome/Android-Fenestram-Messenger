package io.fasthome.fenestram_messenger.push_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendTokenRequest(

    @SerialName("firebase_token")
    val firebaseToken: String

)