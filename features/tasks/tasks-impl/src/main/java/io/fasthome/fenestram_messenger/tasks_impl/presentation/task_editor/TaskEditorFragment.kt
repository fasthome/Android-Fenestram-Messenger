package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.databinding.FragmentTasksBinding
import io.fasthome.fenestram_messenger.uikit.theme.Theme


class TaskEditorFragment : BaseFragment<TaskEditorState, TaskEditorEvent>(R.layout.fragment_tasks) {

    override val vm: TaskEditorViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentTasksBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun syncTheme(appTheme: Theme): Unit = with(binding) {
        appTheme.context = requireContext()
    }

    override fun renderState(state: TaskEditorState) = nothingToRender()

    override fun handleEvent(event: TaskEditorEvent) = noEventsExpected()

}