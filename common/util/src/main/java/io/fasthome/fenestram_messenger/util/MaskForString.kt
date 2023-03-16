package io.fasthome.fenestram_messenger.util

fun String.setMask(mask: String) : String {
    val list = mutableListOf<Pair<Char,Int>>()
    var result = this
    mask.forEachIndexed { index, c ->
        if (c == ' ' || c == '-') {
            list.add(Pair(c, index))
        }
    }
    list.sortBy { it.second }
    try { // TODO: IndexOutOfBoundsException из-за заглушки в контактах, убрать вместе с заглушкой DepartmentService.getDepartments()
        list.forEach { pair ->
            result = result.replaceRange(pair.second, pair.second, pair.first.toString())
        }
        return result
    } catch (e: IndexOutOfBoundsException) {
        return this
    }
}

fun String.setMaskByCountry(country: Country): String {
    return this.setMask(country.mask)
}

enum class Country(val mask: String) {
    RUSSIA("+# ### ###-##-##")
}