package io.fasthome.fenestram_messenger.tasks_api.mapper

import android.content.Context
import io.fasthome.fenestram_messenger.tasks_api.R
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.android.color
import java.util.*

class TaskMapper(private val context: Context) {

    lateinit var appTheme: Theme
    var background = R.drawable.shape_bg_2_10dp
    var selfUserId: Long? = null

    fun mapToTaskCardViewItem(task: Task, selfUserId: Long): ConfeeTaskCardState {
        this.selfUserId = selfUserId

        return when (task.status) {
            Task.Status.DONE -> mapDoneTask(task)
            else -> {
                with(task) {
                    ConfeeTaskCardState(
                        number = " # " + number.toString().padStart(6, '0'),
                        title = title,
                        customer = ConfeeTaskCardState.UserViewItem(task.customer.name, task.customer.avatar),
                        customerLabel = getCustomerLabel(customer.id, executor.id),
                        executor = getTaskExecutor(this),
                        participants = participants?.map { ConfeeTaskCardState.UserViewItem(it.name, it.avatar) },
                        priority = priority.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        priorityStrokeColor = getPriorityStrokeColor(priority),
                        status = mapToStatusViewItem(status),
                        messageId = messageId.toString(),
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        taskType = getTaskType(executor.id),
                        taskCreator = null,
                        style = getTaskStyle()
                    )
                }
            }
        }
    }

    private fun mapDoneTask(task: Task) = with(task) {
        ConfeeTaskCardState(
            number = " # " + number.toString().padStart(6, '0'),
            title = title,
            customer = null,
            customerLabel = PrintableText.EMPTY,
            executor = null,
            participants = null,
            priority = null,
            priorityStrokeColor = getPriorityStrokeColor(priority),
            status = mapToStatusViewItem(status),
            messageId = messageId.toString(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            taskType = getTaskType(executor.id),
            taskCreator = null,
            style = getTaskStyle(true)
        )
    }


    fun mapToStatusViewItem(taskStatus: Task.Status): ConfeeTaskCardState.StatusViewItem = when (taskStatus) {
        Task.Status.QUEUE -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_queue), appTheme.buttonInactiveColor()
        )
        Task.Status.DONE -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_done), appTheme.buttonInactiveColor()
        )
        Task.Status.IN_WORK -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_in_work),
            context.color(R.color.status_green)
        )
    }

    fun getPriorityStrokeColor(priority: Task.Priority? = null): Int = when (priority) {
        Task.Priority.LOW, Task.Priority.LOWEST -> context.color(R.color.text_1)
        Task.Priority.MEDIUM -> context.color(R.color.main_active)
        Task.Priority.HIGH -> context.color(R.color.status_yellow)
        Task.Priority.HIGHEST -> context.color(R.color.red)
        else -> context.color(R.color.button_inactive_dark)
    }


    private fun getTaskExecutor(task: Task): ConfeeTaskCardState.UserViewItem? = when {
        task.executor.id == selfUserId || task.customer.id == task.executor.id -> null
        else -> ConfeeTaskCardState.UserViewItem(task.executor.name, task.executor.avatar)
    }

    private fun getTaskType(executorId: Long): PrintableText = when {
        selfUserId == executorId -> PrintableText.StringResource(R.string.task_card_self)
        else -> PrintableText.StringResource(R.string.task_card_other)
    }

    private fun getCustomerLabel(customerId: Long, executorId: Long): PrintableText = when {
        customerId == executorId -> PrintableText.StringResource(R.string.task_card_customer_executor_label)
        else -> PrintableText.StringResource(R.string.task_card_customer_label)
    }


    private fun getTaskStyle(done: Boolean = false): ConfeeTaskCardState.Style = ConfeeTaskCardState.Style(
        background,
        appTheme.bg01Color(),
        if (done) appTheme.text1Color() else appTheme.text0Color()
    )
}