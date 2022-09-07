/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.Flow


interface AuthFeature {

    val authNavigationContract: NavigationContractApi<NoParams, AuthResult>

    val personalDataNavigationContract: NavigationContractApi<PersonalDataParams, AuthResult>

    suspend fun getUserId(): CallResult<Long?>

    suspend fun getUsers(): CallResult<List<User>>

    suspend fun isUserAuthorized(): CallResult<Boolean>

    suspend fun logout(): CallResult<Unit>

    /**
     * Поток событий, по которым должен происходить переход на экран авторизации.
     * Его предполагается обрабатывать только в MainActivityViewModel.
     */
    val startAuthEvents: Flow<AuthEvent>

    sealed class AuthResult : Parcelable {

        @Parcelize
        object Success : AuthResult()

        @Parcelize
        object Canceled : AuthResult()
    }

    sealed class AuthEvent : Parcelable {

        @Parcelize
        object LaunchWelcome : AuthEvent()
    }

    data class User(
        val id: Long,
        val name: String,
        val phone : String
    )

    @Parcelize
    data class PersonalDataParams(
        val username: String?,
        val nickname: String?,
        val birth: String?,
        val email: String?,
        val avatar: String?
    ) : Parcelable
}