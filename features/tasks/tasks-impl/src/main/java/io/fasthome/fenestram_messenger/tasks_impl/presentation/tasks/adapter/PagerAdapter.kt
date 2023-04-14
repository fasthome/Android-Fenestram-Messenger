package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.tasks_impl.databinding.FragmentTasksViewpager2Binding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model.PagerItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.setPrintableText

class PagerAdapter : AsyncListDifferDelegationAdapter<PagerItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate()
    )
)

fun createContactsAdapterDelegate() =
    adapterDelegateViewBinding<PagerItem, FragmentTasksViewpager2Binding>(
        FragmentTasksViewpager2Binding::inflate,
    ) {

        bindWithBinding {
            rvTasks.adapter = item.taskAdapter
            tvEmpty.setPrintableText(item.emptyText)
            item.taskAdapter.addOnPagesUpdatedListener {
                binding.rvTasks.isVisible = item.taskAdapter.itemCount != 0
                binding.emptyLayout.isVisible = item.taskAdapter.itemCount == 0
            }
        }
    }