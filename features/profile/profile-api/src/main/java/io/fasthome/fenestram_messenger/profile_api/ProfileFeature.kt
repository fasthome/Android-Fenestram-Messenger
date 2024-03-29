/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_api

import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_api.entity.ProfileImageResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ProfileFeature {

    suspend fun uploadProfileImage(readBytes: ByteArray): CallResult<ProfileImageResult>
    suspend fun sendPersonalData(personalData: PersonalData): CallResult<PersonalData>
    suspend fun getPersonalData(): CallResult<PersonalData>

    val profileNavigationContract: NavigationContractApi<NoParams, NoResult>

}
