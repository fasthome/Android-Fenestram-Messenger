/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.service

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.contacts_api.model.DivisionModel
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.DepartmentsMapper
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.SendContactsRequest
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class DepartmentService(
    clientFactory: NetworkClientFactory,
    private val departmentsMapper: DepartmentsMapper,
    private val environment: Environment,
) {
    private val client = clientFactory.create()

    suspend fun getDepartments(): List<DepartmentModel> {
        // TODO: !!! Request
        // DepartmentResponse()
        val employees = listOf(
            Contact(
                id = 1,
                userId = 1,
                phone = "+79876543210",
                userName = "Дмитрий Иванов",
                user = User(1)
            ),
            Contact(
                id = 2,
                userId = 2,
                phone = "+79876543210",
                userName = "Данила Иванов",
                user = User(2)
            ), Contact(
                id = 2,
                userId = 2,
                phone = "+79876543210",
                userName = "Владислав Иванов",
                user = User(2)
            )
        )
        val divisionModels = listOf(
            DivisionModel(
                id = 1,
                title = "Отдел мобильной разработки",
                employee = employees
            ),
            DivisionModel(
                id = 2,
                title = "Отдел Backend разработки",
                employee = employees
            ),
            DivisionModel(
                id = 3,
                title = "Отдел Frontend разработки",
                employee = employees
            )
        )
        return listOf(
            DepartmentModel(
                id = 1,
                title = "IT департамент",
                division = divisionModels
            ),
            DepartmentModel(
                id = 2,
                title = "Департамент финансов",
                division = divisionModels
            )
        )
    }

    suspend fun getContacts(): List<Contact> =
        client
            .runGet<BaseResponse<List<ContactsResponse>>>(
                path = "contacts"
            ).requireData()
            .let(departmentsMapper::responseToContactsList)

    suspend fun sendContacts(contacts: List<Contact>) {
        return client
            .runPost<SendContactsRequest, BaseResponse<Unit>>(
                path = "contacts",
                body = SendContactsRequest(departmentsMapper.contactListToRequest(contacts))
            )
            .requireData()
    }

}
