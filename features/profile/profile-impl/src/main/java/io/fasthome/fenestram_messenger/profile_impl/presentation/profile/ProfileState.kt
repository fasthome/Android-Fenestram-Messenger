/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import io.fasthome.fenestram_messenger.util.PrintableText

data class ProfileState(
    val username : PrintableText?,
    val nickname : PrintableText?,
    val birth : PrintableText?,
    val email : PrintableText?,
    val avatarUrl: String?
)

