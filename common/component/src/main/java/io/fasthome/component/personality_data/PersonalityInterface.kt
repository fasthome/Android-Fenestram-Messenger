/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import io.fasthome.fenestram_messenger.auth_api.UserDetail
import kotlinx.coroutines.flow.Flow

interface PersonalityInterface {
    val fieldStateChanges: Flow<FillState>

    fun getFields() : UserDetail
    fun setFields(userDetail: UserDetail)

    fun invalidateState()
}

enum class FillState { Empty, Filled, Error }