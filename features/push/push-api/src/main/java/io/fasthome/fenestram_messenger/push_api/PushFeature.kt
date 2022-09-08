/**
 * Created by Dmitry Popov on 05.09.2022.
 */
package io.fasthome.fenestram_messenger.push_api

import android.app.Activity
import android.content.Intent
import io.fasthome.fenestram_messenger.navigation.model.IDeepLinkResult
import io.fasthome.fenestram_messenger.util.CallResult

interface PushFeature {

    suspend fun sendTestPush(): CallResult<Unit>

    suspend fun updateToken(): CallResult<List<String>>

    fun handlePushClick(intent: Intent): IDeepLinkResult?
}