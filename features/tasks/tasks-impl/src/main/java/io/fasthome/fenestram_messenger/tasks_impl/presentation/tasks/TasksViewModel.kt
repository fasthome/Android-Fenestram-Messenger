package io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.tasks_api.mapper.TaskMapper
import io.fasthome.fenestram_messenger.tasks_api.model.EditorMode
import io.fasthome.fenestram_messenger.tasks_api.model.Task
import io.fasthome.fenestram_messenger.tasks_impl.domain.logic.TasksInteractor
import io.fasthome.fenestram_messenger.tasks_impl.presentation.task_editor.TaskEditorNavigationContract
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.mapper.toTaskViewItem
import io.fasthome.fenestram_messenger.tasks_impl.presentation.tasks.model.TaskViewItem
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import kotlinx.coroutines.flow.Flow

class TasksViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val tasksInteractor: TasksInteractor,
    val taskMapper: TaskMapper
) : BaseViewModel<TasksState, TasksEvent>(router, requestParams) {

    private var mockSelfUserId: Long = 0
    private val loadDataHelpers = mutableListOf<PagingDataViewModelHelper>()

    private val taskEditorLauncher = registerScreen(TaskEditorNavigationContract) { result ->
        when (result) {
            is TaskEditorNavigationContract.Result.TaskCreated -> {
                //TODO
            }
            TaskEditorNavigationContract.Result.Canceled -> {
                //TODO
            }
            TaskEditorNavigationContract.Result.TaskDeleted -> {
                //TODO
            }
            TaskEditorNavigationContract.Result.TaskEdited -> {
                //TODO
            }
        }
    }

    override fun createInitialState(): TasksState {
        return TasksState(
            selectedTab = Tabs.SELF
        )
    }

    fun fetchTasks(type: String): Flow<PagingData<TaskViewItem>> {
        loadDataHelpers.add(PagingDataViewModelHelper())
        return loadDataHelpers.last().getDataFlow(
            getItems = {
                tasksInteractor.getTasks(type)
            },
            getCachedSelectedId = { null },
            mapDataItem = {
                return@getDataFlow it.toTaskViewItem(taskMapper, mockSelfUserId)
            },
            getItemId = { it.number },
            getItem = { null },
        ).cachedIn(viewModelScope)
    }

    fun onTabSelected(pos: Int) {
        loadDataHelpers[pos].invalidateSource()
        updateState { state -> state.copy(selectedTab = Tabs.values()[pos]) }
    }

    fun onSearchClicked() {
        //TODO
    }

    fun onFilterClicked() {
        //TODO
    }

    fun onTaskCardClicked(task: Task) {
        sendEvent(TasksEvent.ShowTaskMenu(task))
    }

    fun onViewEdit(task: Task, editorMode: EditorMode) {
        taskEditorLauncher.launch(
            TaskEditorNavigationContract.Params(
                task = task,
                mode = editorMode,
            )
        )
    }

    fun onChat(userId: Long) {
        // TODO
    }

    fun onArchive(task: Task) {
        // TODO
    }


    fun onDelete(task: Task) {
        // TODO
    }
}