/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

class SettingsViewItem(
    @DrawableRes val icon: Int,
    val title: PrintableText,
    inline val onItemClicked: () -> Unit,
)