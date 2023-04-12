package io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.R
import io.fasthome.fenestram_messenger.tasks_impl.domain.logic.TasksInteractor
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.mapper.mapToPriority
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.mapper.mapToStatus
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.mapper.mapToTaskEditorState
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionType
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.model.SelectionViewItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText

class TaskEditorViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: TaskEditorNavigationContract.Params,
    private val tasksInteractor: TasksInteractor,
    private val taskMapper: TaskMapper
) : BaseViewModel<TaskEditorState, TaskEditorEvent>(router, requestParams) {

    var appTheme: Theme? = null
    var mockSelfUserId = 0L
    var currentTask: Task = params.task
    var currentMode: EditorMode = params.mode

    override fun createInitialState(): TaskEditorState {
        return params.task.mapToTaskEditorState(params.mode, mockSelfUserId, null)
    }

    fun syncTheme(appTheme: Theme) {
        taskMapper.appTheme = appTheme
        updateState { currentTask.mapToTaskEditorState(params.mode, mockSelfUserId, taskMapper) }
        checkReadyStatus()
    }

    override fun onBackPressed(): Boolean {
        exitWithResult(TaskEditorNavigationContract.createResult(TaskEditorNavigationContract.Result.Canceled))
        return true
    }

    fun onTitleChanged(text: String) {
        currentTask = currentTask.copy(title = text)
        checkReadyStatus()
    }

    fun onDescriptionChanged(text: String) {
        currentTask = currentTask.copy(description = text)
    }

    fun onHistoryClicked() {
        //TODO
    }

    fun onMenuClicked() {
        sendEvent(TaskEditorEvent.ShowActionMenu(editEnabled = currentMode == EditorMode.VIEW))
    }

    fun onClearTitleClicked() {
        sendEvent(TaskEditorEvent.ClearTitle)
    }

    fun onClearDescriptionClicked() {
        sendEvent(TaskEditorEvent.ClearDescription)
    }

    fun onCustomerChatClicked() {
        //TODO
    }

    fun onParticipantChooseClicked() {
        //TODO open partipiciant selection
    }

    fun onStatusClicked() {
        sendEvent(
            TaskEditorEvent.ShowSelectionDialog(
                PrintableText.StringResource(R.string.task_editor_select_title_status),
                createStatusList()
            )
        )
    }

    fun onPriorityClicked() {
        sendEvent(
            TaskEditorEvent.ShowSelectionDialog(
                PrintableText.StringResource(R.string.task_editor_select_title_priority),
                createPriorityList()
            )
        )
    }

    fun onDialogSelection(selectionViewItem: SelectionViewItem) {
        when (val selectionType = selectionViewItem.selectionType) {
            SelectionType.EXECUTOR_SELF -> {
                currentTask = currentTask.copy(executor = User(mockSelfUserId))
            }
            SelectionType.EXECUTOR_OTHER -> {
                //TODO launch ? selection
            }
            SelectionType.EXECUTOR_COMPANION -> {
                //TODO launch chat companion selection
            }
            SelectionType.EXECUTOR_CHAT_USER -> {
                //TODO launch chat users companion selection
            }

            SelectionType.PRIORITY_LOWEST, SelectionType.PRIORITY_LOW, SelectionType.PRIORITY_MEDIUM,
            SelectionType.PRIORITY_HIGH, SelectionType.PRIORITY_HIGHEST -> {
                currentTask = currentTask.copy(priority = selectionType.mapToPriority())
            }

            SelectionType.STATUS_QUEUE, SelectionType.STATUS_IN_WORK, SelectionType.STATUS_DONE -> {
                currentTask = currentTask.copy(status = selectionType.mapToStatus())
            }
        }
        updateState { currentTask.mapToTaskEditorState(currentMode, mockSelfUserId, taskMapper) }
        checkReadyStatus()
    }

    private fun checkReadyStatus() {
        if (currentTask.title.isNotBlank() && currentTask.executor.name.isNotEmpty()) {
            sendEvent(TaskEditorEvent.ToggleReadyButton(isEnabled = true))
        } else sendEvent(TaskEditorEvent.ToggleReadyButton(isEnabled = false))

    }

    fun onCancelClicked() {
        when (currentMode) {
            EditorMode.CREATE -> {
                onBackPressed()
                return
            }
            else -> currentMode = EditorMode.VIEW
        }
        updateState { currentTask.mapToTaskEditorState(currentMode, mockSelfUserId, taskMapper) }
    }

    fun onReadyClicked() {
        when (currentMode) {
            EditorMode.CREATE -> {
                //TODO create request
                exitWithResult(TaskEditorNavigationContract.createResult(TaskEditorNavigationContract.Result.TaskCreated))

            }
            EditorMode.EDIT -> {
                //TODO update request
                exitWithResult(TaskEditorNavigationContract.createResult(TaskEditorNavigationContract.Result.TaskEdited))
            }
            EditorMode.VIEW -> return
        }
    }

    fun onExecutorActionClicked() {
        if (currentViewState.isEditMode) {
            sendEvent(
                TaskEditorEvent.ShowSelectionDialog(
                    PrintableText.StringResource(R.string.task_editor_select_title_executor),
                    createExecutorList()
                )
            )
        } else {
            //TODO open chat
        }
    }

    fun onEdit() {
        currentMode = EditorMode.EDIT
        updateState { currentTask.mapToTaskEditorState(currentMode, mockSelfUserId, taskMapper) }
    }

    fun onArchive() {
        // TODO
    }

    fun onDelete() {
        // TODO delete request
        exitWithResult(TaskEditorNavigationContract.createResult(TaskEditorNavigationContract.Result.TaskDeleted))
    }

    private fun createExecutorList() = listOf(
        SelectionViewItem(
            selectionName = PrintableText.StringResource(R.string.task_editor_select_executor_self),
            selectionType = SelectionType.EXECUTOR_SELF
        ),
        SelectionViewItem(
            selectionName = PrintableText.StringResource(R.string.task_editor_select_executor_companion),
            selectionType = SelectionType.EXECUTOR_COMPANION
        ),
        SelectionViewItem(
            selectionName = PrintableText.StringResource(R.string.task_editor_select_executor_chat_user),
            selectionType = SelectionType.EXECUTOR_CHAT_USER,
            arrowVisible = true
        ),
        SelectionViewItem(
            selectionName = PrintableText.StringResource(R.string.task_editor_select_executor_other),
            selectionType = SelectionType.EXECUTOR_OTHER,
            arrowVisible = true
        ),
    )

    private fun createStatusList(): List<SelectionViewItem> {
        return listOf(
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_status_queue),
                selectionType = SelectionType.STATUS_QUEUE,
                radioButtonVisible = true,
                isChecked = currentTask.status == Task.Status.QUEUE
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_status_in_work),
                selectionType = SelectionType.STATUS_IN_WORK,
                radioButtonVisible = true,
                isChecked = currentTask.status == Task.Status.IN_WORK
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_status_done),
                selectionType = SelectionType.STATUS_DONE,
                radioButtonVisible = true,
                isChecked = currentTask.status == Task.Status.DONE
            ),
        )
    }

    private fun createPriorityList(): List<SelectionViewItem> {
        return listOf(
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_priority_lowest),
                selectionType = SelectionType.PRIORITY_LOWEST,
                radioButtonVisible = true,
                isChecked = currentTask.priority == Task.Priority.LOWEST
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_priority_low),
                selectionType = SelectionType.PRIORITY_LOW,
                radioButtonVisible = true,
                isChecked = currentTask.priority == Task.Priority.LOW
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_priority_medium),
                selectionType = SelectionType.PRIORITY_MEDIUM,
                radioButtonVisible = true,
                isChecked = currentTask.priority == Task.Priority.MEDIUM
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_priority_high),
                selectionType = SelectionType.PRIORITY_HIGH,
                radioButtonVisible = true,
                isChecked = currentTask.priority == Task.Priority.HIGH
            ),
            SelectionViewItem(
                selectionName = PrintableText.StringResource(R.string.task_card_priority_highest),
                selectionType = SelectionType.PRIORITY_HIGHEST,
                radioButtonVisible = true,
                isChecked = currentTask.priority == Task.Priority.HIGHEST
            ),
        )
    }
}