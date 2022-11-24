package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class ContentResponse(
    var name: String,
    /**
     * Расширение файла, напр. ".txt"
     */
    var extension: String,
    /**
     * Размер файла указанный в мегабайтах
     */
    var size: Int,
    var url: String,
) : Parcelable