package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ContactAddViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface
) : BaseViewModel<ContactAddState, ContactAddEvent>(router, requestParams) {

    override fun createInitialState(): ContactAddState {
        return ContactAddState(1)
    }

    fun navigateBack() {
        router.exit()
    }
}