package io.fasthome.component.permission

interface PermissionInterface {

    fun has(permission: String): Boolean

    /**
     * @param canOpenSettings если true, то при установленном "never ask again" будет показан диалог
     * для перехода в системные настройки приложения.
     */
    suspend fun request(permission: String, canOpenSettings: Boolean = true): Boolean
}