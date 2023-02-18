package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.settings_impl.databinding.HolderSettingsBinding
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.model.SettingsViewItem
import io.fasthome.fenestram_messenger.util.*

class SettingsAdapter() : AsyncListDifferDelegationAdapter<SettingsViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createSettingsDelegate()
    )
)

private fun createSettingsDelegate() = adapterDelegateViewBinding<SettingsViewItem, HolderSettingsBinding>(
    HolderSettingsBinding::inflate
) {

    bindWithBinding {
        icon.setImageResource(item.icon)
        title.setPrintableText(item.title)
        item.colors?.textColor?.let { it1 -> title.setTextColor(it1) }
        item.colors?.cardColor?.let { it1 -> root.background = it1 }
        root.onClick {
            item.onItemClicked()
        }
    }
}
