package io.fasthome.fenestram_messenger.profile_impl.presentation.profile.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.dolatkia.animatedThemeManager.ThemeManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_impl.databinding.HolderStatusBinding
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model.StatusViewItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.*

class StatusAdapter(onItemClicked: (StatusViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<StatusViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createStatusDelegate(onItemClicked)
        )
    )

private fun createStatusDelegate(onItemClicked: (StatusViewItem) -> Unit) =
    adapterDelegateViewBinding<StatusViewItem, HolderStatusBinding>(
        HolderStatusBinding::inflate
    ) {

        bindWithBinding {
            statusDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(root.context, item.dotColor))
            statusName.setPrintableText(item.name)
            divider.dividerColor = (ThemeManager.instance.getCurrentTheme() as Theme).stroke2Color()
            checkbox.isChecked = item.isChecked
            checkbox.setOnCheckedChangeListener { compoundButton, b ->
                onItemClicked(item)
            }
            root.onClick {
                onItemClicked(item)
            }
        }
    }
