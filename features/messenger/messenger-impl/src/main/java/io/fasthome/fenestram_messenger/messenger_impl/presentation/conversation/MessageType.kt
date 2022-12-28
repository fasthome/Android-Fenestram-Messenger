/**
 * Created by Dmitry Popov on 14.10.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

enum class MessageType (val type : String){
    Text(type = "text"),
    Image(type = "images"),
    Document(type = "documents")
}