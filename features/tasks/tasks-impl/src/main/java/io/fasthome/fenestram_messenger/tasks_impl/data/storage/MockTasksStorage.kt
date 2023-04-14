package io.fasthome.fenestram_messenger.tasks_impl.data.storage

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.tasks_api.model.Task

class MockTasksStorage {

    //selfUserId = 0
    fun getTasks(type: String): List<Task> = when (type) {
        "self" -> _selfTasks
        "control" -> _controlTasks
        "archive" -> _archiveTasks
        else -> listOf()
    }

    fun getTaskByNumber(taskNumber: Long): Task? {
        val tasks = _selfTasks + _controlTasks
        return tasks.find { it.number == taskNumber }
    }

    private var _controlTasks = listOf(
        Task(
            number = -1,
            title = "",
            description = "",
            customer = User(-1),
            executor = User(-1),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = -1,
            createdAt = "Сегодня",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = 1,
            title = "Сделать презентацию",
            description = "1",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Ivan"),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "15 июня, 11:48",
            completedAt = null
        ),
        Task(
            number = 3,
            title = "Добавить фичу",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            participants = listOf(User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я")),
            priority = Task.Priority.HIGH,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = -1,
            title = "",
            description = "",
            customer = User(-1),
            executor = User(-1),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = -1,
            createdAt = "20.01.2023",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = 4,
            title = "Давно сделанное",
            description = "",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен")
            ),
            priority = Task.Priority.HIGHEST,
            status = Task.Status.DONE,
            messageId = 1,
            createdAt = "20 января, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = null
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
        Task(
            number = 4,
            title = "ДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекстДлинныйтекст",
            description = "",
            customer = User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
                User(1, "", "Олег")
            ),
            priority = Task.Priority.LOW,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = "15 июня, 11:48"
        ),
    )


    private var _selfTasks = listOf(
        Task(
            number = -1,
            title = "",
            description = "",
            customer = User(-1),
            executor = User(-1),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = -1,
            createdAt = "Сегодня",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = 2,
            title = "Исправить баг",
            description = "",
            customer = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
            executor = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            participants = null,
            priority = Task.Priority.HIGHEST,
            status = Task.Status.QUEUE,
            messageId = 1,
            createdAt = "12 июня, 11:48",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = -1,
            title = "",
            description = "",
            customer = User(-1),
            executor = User(-1),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = -1,
            createdAt = "20.01.2023",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = 5,
            title = "Моя задача",
            description = "",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            participants = listOf(
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен"),
                User(1, "https://i.imgur.com", "Семен"),
                User(1, "https://i.imgur.com/ilW4zeW.jpeg", "Семен")
            ),
            priority = Task.Priority.LOWEST,
            status = Task.Status.IN_WORK,
            messageId = 1,
            createdAt = "20 января, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = null
        ),
    )

    private val _archiveTasks = listOf(
        Task(
            number = -1,
            title = "",
            description = "",
            customer = User(-1),
            executor = User(-1),
            participants = null,
            priority = Task.Priority.MEDIUM,
            status = Task.Status.IN_WORK,
            messageId = -1,
            createdAt = "Сегодня",
            updatedAt = null,
            completedAt = null
        ),
        Task(
            number = 12,
            title = "Давно сделанное",
            description = "",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = null,
            priority = Task.Priority.HIGHEST,
            status = Task.Status.DONE,
            messageId = 1,
            createdAt = "20 января, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = null
        ),
        Task(
            number = 42,
            title = "Давно сделанное",
            description = "",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = null,
            priority = Task.Priority.HIGHEST,
            status = Task.Status.DONE,
            messageId = 1,
            createdAt = "20 января, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = null
        ),
        Task(
            number = 55,
            title = "Давно сделанное",
            description = "",
            customer = User(0, "https://i.imgur.com/34NeeMh.jpeg", "Я"),
            executor = User(2, "https://i.imgur.com/ilW4zeW.jpeg", "Дмитрий"),
            participants = null,
            priority = Task.Priority.HIGHEST,
            status = Task.Status.DONE,
            messageId = 1,
            createdAt = "20 января, 11:48",
            updatedAt = "20 июня, 11:48",
            completedAt = null
        ),
    )
}