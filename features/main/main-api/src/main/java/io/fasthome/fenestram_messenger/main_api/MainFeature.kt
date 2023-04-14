/**
 * Created by Dmitry Popov on 01.07.2022.
 */
package io.fasthome.fenestram_messenger.main_api

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult

interface MainFeature {

    val mainNavigationContract: NavigationContractApi<NoParams, NoResult>

    enum class TabType {
        Contacts,
        Chats,
        Tasks,
        Profile
    }

    fun returnToMainScreenAndOpenTab(tab: TabType)

}