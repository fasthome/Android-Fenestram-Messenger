package io.fasthome.fenestram_messenger.tasks_api.mapper

import android.content.Context
import io.fasthome.fenestram_messenger.tasks_api.R
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.ConfeeTaskCardState
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.android.color

class TaskMapper(private val context: Context) {

    var appTheme: Theme? = null
    var background = R.drawable.shape_bg_2_10dp
    var selfUserId: Long? = null

    fun mapToTaskCardViewItem(task: Task, selfUserId: Long): ConfeeTaskCardState {
        this.selfUserId = selfUserId

        return when (task.status) {
            Task.Status.DONE -> mapDoneTask(task)
            else -> {
                with(task) {
                    ConfeeTaskCardState(
                        number = mapTaskNumber(number),
                        title = title,
                        customer = ConfeeTaskCardState.UserViewItem(task.customer.name, task.customer.avatar),
                        customerLabel = getCustomerLabel(customer.id, executor.id),
                        executor = getTaskExecutor(this),
                        participants = participants?.map { ConfeeTaskCardState.UserViewItem(it.name, it.avatar) },
                        priority = mapToPriorityViewItem(priority),
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
            priority = mapToPriorityViewItem(null),
            status = mapToStatusViewItem(status),
            messageId = messageId.toString(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            taskType = getTaskType(executor.id),
            taskCreator = null,
            style = getTaskStyle(true)
        )
    }

    fun mapTaskNumber(taskNumber: Long?): String {
        if (taskNumber == null) return ""
        return " # " + taskNumber.toString().padStart(6, '0')
    }

    fun mapToStatusViewItem(taskStatus: Task.Status): ConfeeTaskCardState.StatusViewItem = when (taskStatus) {
        Task.Status.QUEUE -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_queue), appTheme?.buttonInactiveColor() ?: 0
        )
        Task.Status.DONE -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_done), appTheme?.buttonInactiveColor() ?: 0
        )
        Task.Status.IN_WORK -> ConfeeTaskCardState.StatusViewItem(
            PrintableText.StringResource(R.string.task_card_status_in_work),
            context.color(R.color.status_green)
        )
    }

    fun mapToPriorityViewItem(priority: Task.Priority? = null): ConfeeTaskCardState.PriorityViewItem = when (priority) {
        Task.Priority.LOWEST -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.StringResource(R.string.task_card_priority_lowest),
            context.color(R.color.text_1)
        )
        Task.Priority.LOW -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.StringResource(R.string.task_card_priority_low),
            context.color(R.color.text_1)
        )
        Task.Priority.MEDIUM -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.StringResource(R.string.task_card_priority_medium),
            context.color(R.color.main_active)
        )
        Task.Priority.HIGH -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.StringResource(R.string.task_card_priority_high),
            context.color(R.color.status_yellow),
        )
        Task.Priority.HIGHEST -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.StringResource(R.string.task_card_priority_highest),
            context.color(R.color.red)
        )
        else -> ConfeeTaskCardState.PriorityViewItem(
            PrintableText.EMPTY,
            context.color(R.color.button_inactive_dark)
        )
    }


    private fun getTaskExecutor(task: Task): ConfeeTaskCardState.UserViewItem? = when {
        task.executor.id == selfUserId || task.customer.id == task.executor.id -> null
        else -> ConfeeTaskCardState.UserViewItem(task.executor.name, task.executor.avatar)
    }

    private fun getTaskType(executorId: Long): PrintableText = when {
        selfUserId == executorId -> PrintableText.StringResource(R.string.task_card_self)
        else -> PrintableText.StringResource(R.string.task_card_other, "")
    }

    private fun getCustomerLabel(customerId: Long, executorId: Long): PrintableText = when {
        customerId == executorId -> PrintableText.StringResource(R.string.task_card_customer_executor_label)
        else -> PrintableText.StringResource(R.string.task_card_customer_label)
    }


    private fun getTaskStyle(done: Boolean = false): ConfeeTaskCardState.Style = ConfeeTaskCardState.Style(
        background,
        appTheme?.bg01Color() ?: 0,
        if (done) appTheme?.text1Color() ?: 0 else appTheme?.text0Color() ?: 0
    )
}