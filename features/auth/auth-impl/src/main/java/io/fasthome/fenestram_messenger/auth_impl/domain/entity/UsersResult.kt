package io.fasthome.fenestram_messenger.auth_impl.domain.entity

sealed class UsersResult {
    object ConnectionError : UsersResult()
    class Success(
        val users : List<User>
    ) : UsersResult()
}

class User(
    val id: Long,
    val name: String
)