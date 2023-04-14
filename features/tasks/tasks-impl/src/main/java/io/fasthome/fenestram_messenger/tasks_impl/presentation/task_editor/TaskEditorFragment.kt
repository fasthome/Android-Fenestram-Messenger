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
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.databinding.FragmentTaskEditorBinding
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.dialog.SelectionDialog
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.dialog.TasksDialog
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.custom_view.HooliDatePicker
import io.fasthome.fenestram_messenger.uikit.custom_view.task_card.TaskCardParticipantAdapter
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.android.*


class TaskEditorFragment : BaseFragment<TaskEditorState, TaskEditorEvent>(R.layout.fragment_task_editor) {

    override val vm: TaskEditorViewModel by viewModel(
        getParamsInterface = TaskEditorNavigationContract.getParams,
    )

    private val binding by fragmentViewBinding(FragmentTaskEditorBinding::bind)

    private val participantAdapter = TaskCardParticipantAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        tvExecutorLabel.text = getString(R.string.task_card_executor_label, "")
        tvParticipantLabel.text = getString(R.string.task_card_participant_label, "")

        btnBack.onClick(vm::onBackPressed)
        btnHistory.onClick(vm::onHistoryClicked)
        btnActionMenu.setOnSingleClickListener { vm.onMenuClicked() }
        btnTitleClear.onClick(vm::onClearTitleClicked)
        btnDescriptionClear.onClick(vm::onClearDescriptionClicked)
        ibCustomerChat.onClick(vm::onCustomerChatClicked)
        clExecutor.setOnSingleClickListener { vm.onExecutorActionClicked() }
        tvOriginalMessage.onClick(vm::onOriginalMessageClicked)

        etTitle.addTextChangedListener { vm.onTitleChanged(etTitle.text.toString()) }
        etDescription.addTextChangedListener { vm.onDescriptionChanged(etDescription.text.toString()) }

        HooliDatePicker(tvDeadline).registerDatePicker(childFragmentManager) //TODO переделать в timePicker

        rvParticipant.adapter = participantAdapter
        rvParticipant.addItemDecoration(SpacingItemDecoration { index, _ ->
            if (index > 0) Rect((-5).dp, 0.dp, 0.dp, 0.dp)
            else Rect(0.dp, 0.dp, 0.dp, 0.dp)
        })
        flParticipants.setOnSingleClickListener { vm.onParticipantChooseClicked() }

        btnStatus.setOnSingleClickListener { vm.onStatusClicked() }
        btnPriority.setOnSingleClickListener { vm.onPriorityClicked() }

        btnCancel.onClick(vm::onCancelClicked)
        btnReady.onClick(vm::onReadyClicked)
    }

    override fun onPause() {
        super.onPause()

        vm.saveStateOnPause()
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

        btnCancel.setDrawableBackgroundColor(appTheme.buttonInactiveColor())
    }

    override fun renderState(state: TaskEditorState) = with(binding) {
        tvMode.setPrintableText(state.modeText)
        tvTaskNumber.setPrintableText(state.taskNumber)
        tvCreatedAt.setPrintableText(state.createdAt)
        tvCreatedAtLabel.isVisible = state.editorMode != EditorMode.CREATE
        btnHistory.isVisible = state.editorMode == EditorMode.VIEW
        btnActionMenu.isVisible = state.editorMode != EditorMode.CREATE
        tvOriginalMessage.isVisible = state.originalMessageVisible

        etTitle.setText(state.title)
        etTitle.isEnabled = state.isEditMode
        etTitle.setDrawableBackgroundColor(state.style.boxBackgroundColor)
        etTitle.setDrawableBackgroundStroke(state.style.boxStrokeColor, state.style.boxStrokeDash)
        btnTitleClear.isVisible = state.isEditMode

        etDescription.setText(state.description)
        etDescription.isEnabled = state.isEditMode
        etDescription.setDrawableBackgroundColor(state.style.boxBackgroundColor)
        etDescription.setDrawableBackgroundStroke(state.style.boxStrokeColor, state.style.boxStrokeDash)
        btnDescriptionClear.isVisible = state.isEditMode

        tvCustomerName.text = state.customerItem.name
        ivCustomerAvatar.loadAvatarWithGradient(state.customerItem.avatar, state.customerItem.name)
        ibCustomerChat.isVisible = !state.isEditMode

        tvExecutorName.setPrintableText(state.executorItem.name)
        clExecutor.isClickable = state.isEditMode
        clExecutor.setDrawableBackgroundColor(state.style.boxBackgroundColor)
        clExecutor.setDrawableBackgroundStroke(state.style.boxStrokeColor, state.style.boxStrokeDash)
        state.executorItem.avatar?.let {
            ivExecutorAvatar.isVisible = true
            ivExecutorAvatar.loadAvatarWithGradient(
                state.executorItem.avatar,
                (state.executorItem.name as? PrintableText.Raw)?.s
            )
        } ?: run {
            ivExecutorAvatar.isVisible = false
        }
        tvExecutorName.setDrawableRight(state.executorItem.actionBackground)
        tvExecutorName.setDrawableRightColor(state.executorItem.actionBackgroundColor)

        tvDeadline.text = state.deadline
        tvDeadline.isClickable = state.isEditMode
        tvDeadline.setDrawableBackgroundColor(state.style.boxBackgroundColor)
        tvDeadline.setDrawableBackgroundStroke(state.style.boxStrokeColor, state.style.boxStrokeDash)
        tvDeadline.setDrawableRight(state.style.calendarIcon)

        btnParticipantChoose.isVisible = state.isEditMode
        flParticipants.isClickable = state.isEditMode
        flParticipants.setDrawableBackgroundColor(state.style.boxBackgroundColor)
        flParticipants.setDrawableBackgroundStroke(state.style.boxStrokeColor, state.style.boxStrokeDash)
        participantAdapter.items = state.participants
        tvParticipantEmpty.isVisible = state.participants.isEmpty()

        btnPriority.setPrintableText(state.priorityItem.priority)
        btnPriority.isEnabled = state.isEditMode
        btnPriority.background.setColor(state.priorityItem.priorityColor)
        btnPriority.setDrawableRight(state.style.priorityStatusIcon)

        btnStatus.setPrintableText(state.statusItem.status)
        btnStatus.isEnabled = state.isEditMode
        btnStatus.background.setColor(state.statusItem.statusColor)
        btnStatus.setDrawableRight(state.style.priorityStatusIcon)

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
                        vm.onDialogSelection(it.selectionType)
                    }
                ).show()
            }
            is TaskEditorEvent.ShowActionMenu -> {
                val onEdit = if (event.isEditEnabled) ({ vm.onEdit() }) else null
                TasksDialog.create(
                    this@TaskEditorFragment,
                    onEdit = onEdit,
                    onArchive = vm::onArchive,
                    onDelete = vm::onDelete
                ).show()
            }
            is TaskEditorEvent.ToggleReadyButton -> {
                binding.btnReady.isEnabled = event.isEnabled
                if (event.isEnabled) binding.btnReady.setDrawableBackgroundColor(getTheme().mainActive())
                else binding.btnReady.setDrawableBackgroundColor(getTheme().buttonInactiveColor())
            }
        }
    }

}