/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.domain.logic.OuterTabNavigator
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.provideSavedState
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.requestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.*
import io.fasthome.fenestram_messenger.navigation.model.createParams

class MainViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    savedStateHandle: SavedStateHandle,
    private val features: Features,
    private val outerTabNavigator: OuterTabNavigator
) : BaseViewModel<MainState, MainEvent>(router, requestParams) {

    class Features(
        val chatsFeature: MessengerFeature,
        val contactsFeature: ContactsFeature,
        val profileFeature: ProfileFeature
    )

    private val fragmentsStack = Stack<MainFeature.TabType>()

    init {

        val savedState = savedStateHandle.provideSavedState { SavedState(tabsStack = fragmentsStack) }
        val tabsStack = savedState?.tabsStack ?: Stack()

        if (tabsStack.isNotEmpty()) {
            tabsStack.forEach {
                fragmentsStack.add(it)
            }
        } else {
            fragmentsStack.add(MainFeature.TabType.Chats)
        }

        viewModelScope.launch {
            outerTabNavigator.tabToOpenFlow
                .collectWhenViewActive()
                .collect { newTab ->
                    updateState { MainState(currentTab = newTab) }
                }
        }

    }

    override fun createInitialState(): MainState {
        return MainState(currentTab = fragmentsStack.peek())
    }

    override fun onBackPressed(): Boolean {
        if (fragmentsStack.size <= 1) {
            return false
        }

        fragmentsStack.pop()
        val tabToOpen = fragmentsStack.peek()

        updateState { MainState(currentTab = tabToOpen) }
        return true
    }

    fun onShowFragment(tab: MainFeature.TabType) {
        if (fragmentsStack.peek() == tab) {
            return
        }

        fragmentsStack.remove(tab)
        fragmentsStack.push(tab)
        updateState { MainState(currentTab = tab) }
    }

    fun buildFragment(type: MainFeature.TabType): Fragment {
        return when (type) {
            MainFeature.TabType.Chats -> features.chatsFeature.messengerNavigationContract.createParams().createFragment()
            MainFeature.TabType.Contacts -> features.contactsFeature.contactsNavigationContract.createParams().createFragment()
            MainFeature.TabType.Profile -> features.profileFeature.profileNavigationContract.createParams().createFragment()
        }.apply {
                this.requestParams = RequestParams.createWithIgnoreResult()
            }
    }

    @Parcelize
    private data class SavedState(
        val tabsStack: Stack<MainFeature.TabType>,
    ) : Parcelable

}