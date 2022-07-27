/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.net.Uri

data class ProfileState(
    val avatar: Uri?,
    val isEdit: Boolean
)