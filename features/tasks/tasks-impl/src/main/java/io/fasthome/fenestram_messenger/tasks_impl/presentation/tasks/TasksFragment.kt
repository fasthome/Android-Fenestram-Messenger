package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.databinding.FragmentTasksBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.adapter.PagerAdapter
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.adapter.TaskAdapter
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.dialog.TasksDialog
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model.PagerItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.collectWhenStarted
import io.fasthome.fenestram_messenger.util.onClick
import kotlinx.coroutines.flow.distinctUntilChanged


class TasksFragment : BaseFragment<TasksState, TasksEvent>(R.layout.fragment_tasks) {

    override val vm: TasksViewModel by viewModel()

    private val pagerAdapter = PagerAdapter()

    private val binding by fragmentViewBinding(FragmentTasksBinding::bind)

    var firstResume = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        viewPager2.adapter = pagerAdapter
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vm.onTabSelected(tab.position)
                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)!!.select()
                super.onPageSelected(position)
            }
        })

        ibSearch.onClick(vm::onSearchClicked)
        ibFilter.onClick(vm::onFilterClicked)
    }

    override fun onResume() {
        super.onResume()
        if (firstResume) {
            subscribeTasks()
            firstResume = false
        }
    }

    override fun syncTheme(appTheme: Theme): Unit = with(binding) {
        appTheme.context = requireContext()
        vm.taskMapper.appTheme = appTheme
        root.setBackgroundColor(appTheme.bg0Color())
        tabLayout.setTabTextColors(appTheme.buttonInactiveColor(), appTheme.mainActive())
    }

    private fun subscribeTasks() {
        val pagerItems = listOf(
            PagerItem(
                TaskAdapter(onTaskCardClicked = { task -> vm.onTaskCardClicked(task) }),
                PrintableText.StringResource(R.string.tasks_self_empty)
            ),
            PagerItem(
                TaskAdapter(onTaskCardClicked = { task -> vm.onTaskCardClicked(task) }),
                PrintableText.StringResource(R.string.tasks_control_empty)
            )
        )

        Tabs.values().forEach { tab ->
            vm.fetchTasks(tab.type)
                .distinctUntilChanged()
                .collectWhenStarted(this@TasksFragment) {
                    pagerItems[tab.ordinal].taskAdapter.submitData(it)
                }
        }

        pagerAdapter.items = pagerItems
    }

    override fun renderState(state: TasksState) = nothingToRender()

    override fun handleEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.ShowTaskMenu -> {
                TasksDialog.create(
                    this@TasksFragment,
                    onEdit = { vm.onViewEdit(event.task, EditorMode.EDIT) },
                    onView = { vm.onViewEdit(event.task, EditorMode.VIEW) },
                    onArchive = { vm.onArchive(event.task) },
                    onChatCustomer = { vm.onChat(event.task.customer.id) },
                    onChatExecutor = { vm.onChat(event.task.executor.id) },
                    onDelete = { vm.onDelete(event.task) }
                ).show()
            }
        }
    }

}