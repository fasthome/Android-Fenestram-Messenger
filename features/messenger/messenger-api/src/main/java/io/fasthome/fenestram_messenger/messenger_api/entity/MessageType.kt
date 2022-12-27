/**
 * Created by Dmitry Popov on 14.10.2022.
 */
package io.fasthome.fenestram_messenger.messenger_api.entity

enum class MessageType (val type : String){
    Text(type = "text"),
    Image(type = "image"),
    Document(type = "documents"),
    Unknown(type = "unknown")
}