package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UploadDocumentResponse(

    @SerialName("filename")
    val filename: String,

    @SerialName("pathToFile")
    val pathToFile: String

)