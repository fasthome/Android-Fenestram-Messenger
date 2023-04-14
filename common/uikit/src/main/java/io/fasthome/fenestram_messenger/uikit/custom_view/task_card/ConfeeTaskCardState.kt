package io.fasthome.fenestram_messenger.uikit.custom_view.task_card

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

data class ConfeeTaskCardState(
    val number: String,
    val title: String,
    val customer: UserViewItem?,
    val customerLabel: PrintableText,
    val executor: UserViewItem?,
    val participants: List<UserViewItem>?,
    val priority: PriorityViewItem,
    val status: StatusViewItem,
    val messageId: String,
    val createdAt: String,
    val updatedAt: String?,
    val taskType: PrintableText,
    val taskCreator: UserViewItem?,
    val style: Style
) {

    data class Style(
        @DrawableRes val background: Int,
        val backgroundColor: Int,
        val textColor: Int
    )

    data class UserViewItem(val name: String, val avatar: String = "")

    data class StatusViewItem(val status: PrintableText, val statusColor: Int)

    data class PriorityViewItem(val priority: PrintableText, val priorityColor: Int)

}