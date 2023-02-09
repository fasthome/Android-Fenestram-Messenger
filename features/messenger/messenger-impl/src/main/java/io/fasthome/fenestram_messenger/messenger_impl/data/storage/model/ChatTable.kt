/**
 * Created by Dmitry Popov on 24.01.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.data.storage.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime

@Entity
class ChatTable(
    @PrimaryKey
    val id: Long?,
    val name: String,
    val users: List<Long>,
    val messages: List<String>,
    val time: ZonedDateTime?,
    val avatar: String?,
    val isGroup: Boolean,
    val pendingMessages: Long
)

@Serializable
data class MessageDb(
    val id: Long,
    val text: String,
    val userSenderId: Long,
    val messageType: String,
    @Serializable(KZonedDateTimeSerializer::class)
    val date: ZonedDateTime?,
    val initiator: String?,
    val chatId: String? = null,
    val isDate: Boolean,
    val isEdited: Boolean,
    val messageStatus: String,
    val replyMessage: MessageDb?,
    val usersHaveRead: List<Long?>?,
    val forwardedMessages: List<String>?,
    val content: List<String>?
)

@Serializable
class ContentDb(
    val name: String,
    val extension: String,
    val size: Float,
    val url: String?,
)

@Serializable
class UserDb(
    val id : Long,
    val phone : String,
    val name : String,
    val nickname : String,
    val email : String,
    val contactName : String?,
    val birth : String,
    val avatar : String,
    val isOnline : Boolean,
    @Serializable(KZonedDateTimeSerializer::class)
    val lastActive : ZonedDateTime
)

object KZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val string = decoder.decodeString()
        return ZonedDateTime.parse(string)
    }
}