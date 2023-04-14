package io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model

import io.fasthome.fenestram_messenger.util.PrintableText

data class StatusViewItem(
    val name: PrintableText,
    var isChecked: Boolean,

    val dotColor: Int

)