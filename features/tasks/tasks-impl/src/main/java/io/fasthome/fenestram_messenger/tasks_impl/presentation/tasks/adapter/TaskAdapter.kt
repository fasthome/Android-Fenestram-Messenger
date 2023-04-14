package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.adapter

import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.databinding.TaskCardViewItemBinding
import io.fasthome.fenestram_messenger.tasks_impl.databinding.TaskHeaderViewItemBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model.TaskViewItem
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.onClick

class TaskAdapter(onTaskCardClicked: (Task) -> Unit) : PagerDelegateAdapter<TaskViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    delegates = listOf(
        createTaskCardAdapterDelegate(onTaskCardClicked),
        createTaskHeaderAdapterDelegate()
    )
)


fun createTaskCardAdapterDelegate(onTaskCardClicked: (Task) -> Unit) =
    createAdapterDelegate(
        inflate = TaskCardViewItemBinding::inflate,
        bind = { item: TaskViewItem.TaskCardViewItem, binding: TaskCardViewItemBinding ->
            binding.root.onClick {
                onTaskCardClicked(item.task)
            }

            with(binding) {
                root.setState(item.taskCardState)
            }
        }
    )

fun createTaskHeaderAdapterDelegate() =
    createAdapterDelegate(
        inflate = TaskHeaderViewItemBinding::inflate,
        bind = { item: TaskViewItem.Header, binding: TaskHeaderViewItemBinding ->
            with(binding) {
                tvCreatedAt.text = item.createdAt
            }
        }
    )
