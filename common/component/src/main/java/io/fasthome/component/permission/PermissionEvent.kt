package io.fasthome.component.permission

sealed interface PermissionEvent {
    data class RequestPermission(val permission: String) : PermissionEvent
}