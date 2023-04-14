package io.fasthome.fenestram_messenger.tasks_api.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val number: Long?,
    val title: String,
    val description: String?,
    val customer: User,
    val executor: User,
    val participants: List<User>?,
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.QUEUE,
    val messageId: Long?,
    val createdAt: String,
    val updatedAt: String?,
    val completedAt: String?
) : Parcelable {


    enum class Priority(val type: String) {
        MEDIUM("medium"),
        LOWEST("lowest"),
        LOW("low"),
        HIGH("high"),
        HIGHEST("highest"),
    }

    enum class Status(val type: String) {
        QUEUE("queue"),
        IN_WORK("in work"),
        DONE("done"),
    }
}