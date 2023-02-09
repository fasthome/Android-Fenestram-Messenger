package io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class FileResponse(
    @SerialName("files")
    val files: List<File>,
)
@kotlinx.serialization.Serializable
class File(
    @SerialName("name")
    val name: String,
    @SerialName("extension")
    val extension: String,
    @SerialName("size")
    val size: Float,
    @SerialName("url")
    val url: String,
)
