/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.fenestram_messenger.util


val REGEX_SEARCH_TERM = "[a-zA-ZЁёА-Яа-я 0-9.]".toRegex()
val REGEX_DIGITS_AND_DOTS_SPACES = "^[0-9. ]*$".toRegex()
val REGEX_DIGITS = "^[0-9]*$".toRegex()
val REGEX_DIGITS_AND_SPECIAL_SYMBOLS = "^[0-9 .,\\-/]*$".toRegex()
val REGEX_PASSWORD = "[a-zA-Z0-9]".toRegex()
val REGEX_LETTERS_AND_SPACE_AND_DASH = "[a-zA-ZЁёА-Яа-я -]".toRegex()
val REGEX_LETTERS_NUMBERS_SPACE_DASH = "[a-zA-ZЁёА-Яа-я 0-9-]".toRegex()

val REGEX_RU_LETTERS_AND_SPACE = "[ЁёА-Яа-я ]".toRegex()
val REGEX_EN_LETTERS_AND_DIGITS_AND_SPACE_AND_DASH_AND_DOT_AND_UNDERSCORE = "[a-zA-Z0-9 ._-]".toRegex()

val CYRILLIC_LETTERS_AND_SPECIAL_SYMBOLS = "^[ЁёА-Яа-я .,\\-/]*\$".toRegex()

val REGEX_NAME_INPUT_FILTER = REGEX_LETTERS_AND_SPACE_AND_DASH
val REGEX_NAME_OUTPUT_FILTER = "[^a-zA-ZЁёА-Яа-я -]".toRegex()

val REGEX_RUS_PHONE_INPUT_FILTER = "[0-9 ()\\-+]".toRegex()
val REGEX_RUS_PHONE_OUTPUT_FILTER = "[^0-9+]".toRegex()

val REGEX_DATE_INPUT_FILTER = "[0-9.]".toRegex()
val REGEX_DATE_OUTPUT_FILTER = "[^0-9.]".toRegex()

val REGEX_EMAIL_INPUT_FILTER = "[a-zA-Z 0-9.!#\$%&‘*+—/=?^_`'\\-\\\\{|}~\"@]".toRegex()
val REGEX_EMAIL_OUTPUT_FILTER = "[*]".toRegex()

val REGEX_ADDRESS_INPUT_FILTER = "[ЁёА-Яа-я 0-9.,\\-№()/]".toRegex()
val REGEX_ADDRESS_OUTPUT_FILTER = "[*]".toRegex()
