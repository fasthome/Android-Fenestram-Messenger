/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl

import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_api.model.PersonalData
import io.fasthome.fenestram_messenger.profile_api.model.ProfileImageResult
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.ProfileNavigationContract
import io.fasthome.fenestram_messenger.util.CallResult

class ProfileFeatureImpl (private val profileInteractor: ProfileInteractor) : ProfileFeature {

    override suspend fun uploadProfileImage(readBytes: ByteArray): CallResult<ProfileImageResult> = profileInteractor.uploadProfileImage(readBytes)

    override suspend fun sendPersonalData(personalData: PersonalData): CallResult<PersonalData> = profileInteractor.sendPersonalData(personalData)

    override suspend fun getPersonalData(): CallResult<PersonalData> = profileInteractor.getPersonalData()

    override val profileNavigationContract = ProfileNavigationContract
}