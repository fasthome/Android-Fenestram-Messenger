package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageReactions(
    @SerialName("&#128077")
    val reaction1: List<Long>,
    @SerialName("&#128076")
    val reaction2: List<Long>,
    @SerialName("&#129505")
    val reaction3: List<Long>,
    @SerialName("&#128064")
    val reaction4: List<Long>,
    @SerialName("&#128163")
    val reaction5: List<Long>,
    @SerialName("&#128165")
    val reaction6: List<Long>,
    @SerialName("&#127820")
    val reaction7: List<Long>,
    @SerialName("&#9940")
    val reaction8: List<Long>,
)