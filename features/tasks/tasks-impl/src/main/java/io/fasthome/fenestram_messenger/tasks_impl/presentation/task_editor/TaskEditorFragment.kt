package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.core.ui.extensions.loadAvatarWithGradient
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.databinding.FragmentTaskEditorBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.dialog.SelectionDialog
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.dialog.TasksDialog
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.custom_view.HooliDatePicker
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.TaskCardParticipantAdapter
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.android.setBackgroundColor
import io.fasthome.fenestram_messenger.util.android.setColor
import io.fasthome.fenestram_messenger.util.android.setStroke
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText


class TaskEditorFragment : BaseFragment<TaskEditorState, TaskEditorEvent>(R.layout.fragment_task_editor) {

    override val vm: TaskEditorViewModel by viewModel(
        getParamsInterface = TaskEditorNavigationContract.getParams,
    )

    private val binding by fragmentViewBinding(FragmentTaskEditorBinding::bind)

    private val participantAdapter = TaskCardParticipantAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.onClick(vm::onBackPressed)
        btnHistory.onClick(vm::onHistoryClicked)
        btnActionMenu.onClick(vm::onMenuClicked)
        btnTitleClear.onClick(vm::onClearTitleClicked)
        btnDescriptionClear.onClick(vm::onClearDescriptionClicked)
        ibCustomerChat.onClick(vm::onCustomerChatClicked)
        clExecutor.onClick(vm::onExecutorActionClicked)

        etTitle.addTextChangedListener { vm.onTitleChanged(etTitle.text.toString()) }
        etDescription.addTextChangedListener { vm.onDescriptionChanged(etDescription.text.toString()) }

        HooliDatePicker(tvDeadline).registerDatePicker(childFragmentManager) //TODO переделать в timePicker

        rvParticipant.adapter = participantAdapter
        rvParticipant.addItemDecoration(SpacingItemDecoration { index, _ ->
            if (index > 0) Rect((-5).dp, 0.dp, 0.dp, 0.dp)
            else Rect(0.dp, 0.dp, 0.dp, 0.dp)
        })
        flParticipants.onClick(vm::onParticipantChooseClicked)

        btnStatus.onClick(vm::onStatusClicked)
        btnPriority.onClick(vm::onPriorityClicked)

        btnCancel.onClick(vm::onCancelClicked)
        btnReady.onClick(vm::onReadyClicked)
    }

    override fun syncTheme(appTheme: Theme): Unit = with(binding) {
        appTheme.context = requireContext()
        vm.syncTheme(appTheme)

        clToolbar.setBackgroundColor(appTheme.bg03Color())
        btnBack.drawable.setColor(appTheme.text1Color())
        tvMode.setTextColor(appTheme.text0Color())
        btnActionMenu.drawable.setColor(appTheme.text1Color())

        svContent.background.setColor(appTheme.bg1Color())
        etTitle.setTextColor(appTheme.text0Color())
        etDescription.setTextColor(appTheme.text0Color())

        tvCustomerName.setTextColor(appTheme.text0Color())
        tvExecutorName.setTextColor(appTheme.text0Color())
        btnParticipantChoose.drawable.setColor(appTheme.stroke1Color())

        tvDeadline.setTextColor(appTheme.text0Color())

        btnCancel.background.setColor(appTheme.buttonInactiveColor())
    }

    override fun renderState(state: TaskEditorState) = with(binding) {
        tvMode.setPrintableText(state.modeText)
        tvTaskNumber.setPrintableText(state.taskNumber)
        tvCreatedAt.setPrintableText(state.createdAt)
        btnHistory.isVisible = state.historyVisible
        btnActionMenu.isVisible = state.actionMenuVisible
        tvCreatedAtLabel.isVisible = state.createdAtLabelVisible
        tvOriginalMessage.isVisible = state.originalMessageVisible

        etTitle.setText(state.title)
        etTitle.isEnabled = state.isEditMode
        etTitle.background.setBackgroundColor(state.textBackgroundColor)
        etTitle.background.setStroke(state.textStrokeColor, 2, state.textStrokeDash, state.textStrokeDash)
        btnTitleClear.isVisible = state.isEditMode

        etDescription.setText(state.description)
        etDescription.isEnabled = state.isEditMode
        etDescription.background.setBackgroundColor(state.textBackgroundColor)
        etDescription.background.setStroke(state.textStrokeColor, 2, state.textStrokeDash, state.textStrokeDash)
        btnDescriptionClear.isVisible = state.isEditMode

        tvCustomerName.text = state.customerName
        ivCustomerAvatar.loadAvatarWithGradient(state.customerAvatar, state.customerName)
        ibCustomerChat.isVisible = !state.isEditMode

        tvExecutorName.setPrintableText(state.executorName)
        clExecutor.isClickable = state.isEditMode
        clExecutor.background.setBackgroundColor(state.textBackgroundColor)
        clExecutor.background.setStroke(state.textStrokeColor, 2, state.textStrokeDash, state.textStrokeDash)
        state.executorAvatar?.let {
            ivExecutorAvatar.isVisible = true
            ivExecutorAvatar.loadAvatarWithGradient(state.customerAvatar, state.customerName)
        } ?: run {
            ivExecutorAvatar.isVisible = false
        }
        ibExecutorAction.setImageResource(state.executorActionBackground)
        ibExecutorAction.drawable.setColor(state.executorActionBackgroundColor)

        tvDeadline.text = state.deadline
        tvDeadline.background.setBackgroundColor(state.textBackgroundColor)
        tvDeadline.background.setStroke(state.textStrokeColor, 2, state.textStrokeDash, state.textStrokeDash)
        tvDeadline.setCompoundDrawablesWithIntrinsicBounds(0, 0, state.calendarIcon, 0)

        btnParticipantChoose.isVisible = state.isEditMode
        flParticipants.isClickable = state.isEditMode
        flParticipants.background.setBackgroundColor(state.textBackgroundColor)
        flParticipants.background.setStroke(state.textStrokeColor, 2, state.textStrokeDash, state.textStrokeDash)
        participantAdapter.items = state.participants
        tvParticipant.isVisible = state.participants.isEmpty()

        btnPriority.text = state.priority
        btnPriority.isEnabled = state.isEditMode
        btnPriority.background.setColor(state.priorityColor)
        btnPriority.setCompoundDrawablesWithIntrinsicBounds(0, 0, state.buttonsIcon, 0)

        btnStatus.setPrintableText(state.statusItem.status)
        btnStatus.isEnabled = state.isEditMode
        btnStatus.background.setColor(state.statusItem.statusColor)
        btnStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, state.buttonsIcon, 0)

        btnCancel.isVisible = state.isEditMode
        btnReady.isVisible = state.isEditMode
    }

    override fun handleEvent(event: TaskEditorEvent) {
        when (event) {
            is TaskEditorEvent.ClearTitle -> binding.etTitle.text.clear()
            is TaskEditorEvent.ClearDescription -> binding.etDescription.text.clear()
            is TaskEditorEvent.ShowSelectionDialog -> {
                SelectionDialog.create(
                    this@TaskEditorFragment,
                    selectionTitle = event.selectionTitle,
                    items = event.items,
                    onItemSelected = {
                        vm.onDialogSelection(it)
                    }
                ).show()
            }
            is TaskEditorEvent.ShowActionMenu -> {
                val onEdit = if (event.editEnabled) ({ vm.onEdit() }) else null
                TasksDialog.create(
                    this@TaskEditorFragment,
                    onEdit = onEdit,
                    onArchive = vm::onArchive,
                    onDelete = vm::onDelete
                ).show()
            }
            is TaskEditorEvent.ToggleReadyButton -> {
                binding.btnReady.isEnabled = event.isEnabled
                if (event.isEnabled) binding.btnReady.background.setColor(getTheme().mainActive())
                else binding.btnReady.background.setColor(getTheme().buttonInactiveColor())
            }
        }
    }

}