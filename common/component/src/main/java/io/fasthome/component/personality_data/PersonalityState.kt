/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import io.fasthome.fenestram_messenger.util.PrintableText

/**
 *
 * @param singleValidate используется для одиночной валидации всех полей без задержки
 */
data class PersonalityState(
    val fields: Map<EditTextKey, Field>,
    val singleValidate: SingleValidate,
    val visibilityIcon: Boolean,
)

data class SingleValidate(
    val singleValidate: Boolean,
    val needValidate: Boolean
)

data class Field(
    val key: EditTextKey,
    val text: PrintableText?,
    val validateText: PrintableText?,
    val visibilityIcon: Boolean,
    val error: PrintableText?
)

enum class EditTextKey {
    UsernameKey,
    NicknameKey,
    BirthdateKey,
    MailKey
}