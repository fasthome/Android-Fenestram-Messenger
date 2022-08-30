/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.data

import io.fasthome.fenestram_messenger.core.environment.Environment

class ProfileImageUrlConverter(private val environment: Environment) {

    fun convert(path: String) =
        environment.endpoints.apiBaseUrl.dropLast(1) + path


}