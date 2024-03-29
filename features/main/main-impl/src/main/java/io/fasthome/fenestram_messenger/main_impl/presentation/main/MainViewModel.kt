/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.domain.logic.OuterTabNavigator
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.provideSavedState
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.createParams
import io.fasthome.fenestram_messenger.navigation.model.requestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.tasks_api.TasksFeature
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.*

class MainViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    savedStateHandle: SavedStateHandle,
    private val features: Features,
    private val outerTabNavigator: OuterTabNavigator,
    private val environment: Environment,
) : BaseViewModel<MainState, MainEvent>(router, requestParams) {

    class Features(
        val chatsFeature: MessengerFeature,
        val contactsFeature: ContactsFeature,
        val profileFeature: ProfileFeature,
        val debugFeature: DebugFeature,
        val pushFeature: PushFeature,
        val messengerFeature: MessengerFeature,
        val tasksFeature: TasksFeature
    )

    private val fragmentsStack = Stack<MainFeature.TabType>()

    private val debugLauncher = registerScreen(features.debugFeature.navigationContract)

    init {
        val savedState =
            savedStateHandle.provideSavedState { SavedState(tabsStack = fragmentsStack) }
        val tabsStack = savedState?.tabsStack ?: Stack()

        if (tabsStack.isNotEmpty()) {
            tabsStack.forEach {
                fragmentsStack.add(it)
            }
        } else {
            fragmentsStack.add(MainFeature.TabType.Chats)
        }

        viewModelScope.launch {
            updateFbToken()

            features.messengerFeature.subscribeUnreadCount()
                .collectWhenViewActive()
                .onEach { badge ->
                    sendEvent(
                        MainEvent.UpdateBadge(
                            count = badge.count.toInt()
                        )
                    )
                }
                .launchIn(viewModelScope)
            outerTabNavigator.tabToOpenFlow
                .collectWhenViewActive()
                .collect { newTab ->
                    updateState {
                        it.copy(currentTab = newTab)
                    }
                }
        }

    }

    override fun createInitialState(): MainState {
        return MainState(
            currentTab = fragmentsStack.peek(),
            debugVisible = environment.isDebug,
            fabVisible = true
        )
    }

    override fun onBackPressed(): Boolean {
        if (fragmentsStack.size <= 1) {
            return false
        }

        fragmentsStack.pop()
        val tabToOpen = fragmentsStack.peek()

        updateState {
            it.copy(currentTab = tabToOpen)
        }
        return true
    }

    fun onShowFragment(tab: MainFeature.TabType) {
        if (fragmentsStack.peek() == tab) {
            return
        }

        fragmentsStack.remove(tab)
        fragmentsStack.push(tab)
        val fabVisible = when (tab) {
            MainFeature.TabType.Contacts,
            MainFeature.TabType.Chats,
            -> true
            else -> false
        }
        updateState {
            it.copy(
                currentTab = tab,
                fabVisible = fabVisible
            )
        }
    }

    fun buildFragment(type: MainFeature.TabType): Fragment {
        return when (type) {
            MainFeature.TabType.Chats -> features.chatsFeature.messengerNavigationContract.createParams(
                MessengerFeature.MessengerParams(
                    chatSelectionMode = false,
                    forwardMessage = null
                )
            ).createFragment()
            MainFeature.TabType.Tasks -> features.tasksFeature.tasksNavigationContract.createParams().createFragment()
            MainFeature.TabType.Contacts -> features.contactsFeature.contactsNavigationContract.createParams()
                .createFragment()
            MainFeature.TabType.Profile -> features.profileFeature.profileNavigationContract.createParams()
                .createFragment()
        }.apply {
            this.requestParams = RequestParams.createWithIgnoreResult()
        }
    }

    fun debugClicked() {
        debugLauncher.launch(NoParams)
    }

    fun fetchUnreadCount() {
        viewModelScope.launch {
            features.messengerFeature
                .fetchUnreadCount().onSuccess { badge ->
                    sendEvent(
                        MainEvent.UpdateBadge(
                            count = badge.count.toInt()
                        )
                    )
                }
        }
    }

    private suspend fun updateFbToken() {
        features.pushFeature.updateToken().onSuccess {
            if (it.isEmpty()) {
                updateFbToken()
            }
        }
    }

    @Parcelize
    private data class SavedState(
        val tabsStack: Stack<MainFeature.TabType>,
    ) : Parcelable

}