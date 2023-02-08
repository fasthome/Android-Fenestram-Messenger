package io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class FileResponse(

    @SerialName("files")
    val files: List<String>,
)