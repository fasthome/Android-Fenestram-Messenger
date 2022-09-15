/**
 * Created by Dmitry Popov on 14.09.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

data class MessagesPage(
    val page : Int,
    val total : Int,
    val messages : List<Message>
)