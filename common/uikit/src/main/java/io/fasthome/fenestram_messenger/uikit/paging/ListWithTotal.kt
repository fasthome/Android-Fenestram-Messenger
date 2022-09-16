package io.fasthome.fenestram_messenger.uikit.paging

/**
 * Список элементов определенной страницы для пагинации и общее кол-во элементов в списке.
 */
data class ListWithTotal<out T>(
    val list: List<T>,
    val totalCount: Int,
)