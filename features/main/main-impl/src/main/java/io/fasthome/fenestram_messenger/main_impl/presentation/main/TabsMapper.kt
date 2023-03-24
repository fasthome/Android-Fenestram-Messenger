package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.view.MenuItem
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.main_impl.R

object TabsMapper {
    fun mapItemIdToTab(item: MenuItem) = when (item.itemId) {
        R.id.contacts -> MainFeature.TabType.Contacts
        R.id.chats -> MainFeature.TabType.Chats
        R.id.tasks -> MainFeature.TabType.Tasks
        R.id.profile -> MainFeature.TabType.Profile
        else -> null
    }

    fun mapTabToItemId(tab: MainFeature.TabType): Int = when (tab) {
        MainFeature.TabType.Contacts -> R.id.contacts
        MainFeature.TabType.Chats -> R.id.chats
        MainFeature.TabType.Tasks -> R.id.tasks
        MainFeature.TabType.Profile -> R.id.profile
    }
}