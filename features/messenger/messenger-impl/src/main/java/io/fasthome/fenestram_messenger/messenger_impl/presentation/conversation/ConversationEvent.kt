package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface ConversationEvent {
    object OpenMenuEvent : ConversationEvent

    class SendLoading(val isLoading: Boolean): ConversationEvent
    class ShowDeleteChatDialog(val id: Long) : ConversationEvent
    object ShowChatDeletedDialog : ConversationEvent
    object OpenFilePicker : ConversationEvent
    object OpenCamera: ConversationEvent
    object OpenImagePicker: ConversationEvent
    class UpdateScrollPosition(val scrollPosition: Int) : ConversationEvent
    object InvalidateList : ConversationEvent
    class ShowSelectFromDialog(val attachedContent: List<Content>) : ConversationEvent
    class ShowUsersTags(val users: List<User>) : ConversationEvent
    class ShowPersonDetailDialog(val selectedPerson: PersonDetail) : ConversationEvent
    class UpdateInputUserTag(val nickname: String) : ConversationEvent
    class ShowErrorSentDialog(val conversationViewItem: ConversationViewItem.Self) :
        ConversationEvent

    class ShowSelfMessageActionDialog(val conversationViewItem: ConversationViewItem.Self) :
        ConversationEvent

    class ShowReceiveMessageActionDialog(val conversationViewItem: ConversationViewItem.Receive) :
        ConversationEvent

    class ShowGroupMessageActionDialog(val conversationViewItem: ConversationViewItem.Group) :
        ConversationEvent

    class DotsEvent(val userStatus: PrintableText, val userStatusDots: PrintableText) :
        ConversationEvent

    class ToggleToolbarClickable(val clickable: Boolean) : ConversationEvent

    class ExtraText(val text : String) : ConversationEvent
}