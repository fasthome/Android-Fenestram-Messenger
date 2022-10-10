/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.fenestram_messenger.data

import io.fasthome.fenestram_messenger.core.environment.Environment

class ProfileImageUrlConverter(private val environment: Environment) {

    fun convert(path: String?) =
        if (path.isNullOrEmpty() || path == "null") {
            ""
        } else {
            environment.endpoints.baseUrl.dropLast(1) + path
        }

    fun extractPath(imageUrl: String?) =
        if (imageUrl.isNullOrEmpty() || imageUrl == "null") {
            ""
        } else {
            imageUrl.substring(environment.endpoints.baseUrl.length - 1, imageUrl.length)
        }

}