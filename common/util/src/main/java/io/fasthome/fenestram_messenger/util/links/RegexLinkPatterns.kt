package io.fasthome.fenestram_messenger.util.links

import java.util.regex.Pattern

val WEB_URL_PATTERN: Pattern = Pattern.compile("(https?://)?([\\da-zА-я\\-]+)\\.([a-zА-я]{1,6})([#?=%,_&/\\w.-]*)*/?")

val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

val PHONE_PATTERN: Pattern = Pattern.compile(
    //todo пока только русские номера
    "(\\+7|7|8)[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}"
)

val USER_TAG_PATTERN: Pattern = Pattern.compile(
    "(\\@)" +
            "[a-zA-ZЁёА-Яа-я0-9_]{1,22}"
)