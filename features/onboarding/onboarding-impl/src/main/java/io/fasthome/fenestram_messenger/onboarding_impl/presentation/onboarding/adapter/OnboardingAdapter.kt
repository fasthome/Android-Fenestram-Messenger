package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.adapter

import androidx.core.content.res.ResourcesCompat
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentViewpager2Binding
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.Page
import io.fasthome.fenestram_messenger.util.*

class OnboardingAdapter : AsyncListDifferDelegationAdapter<Page>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate()
    )
)



fun createContactsAdapterDelegate() =
    adapterDelegateViewBinding<Page, FragmentViewpager2Binding>(
        FragmentViewpager2Binding::inflate,
    ) {
        bindWithBinding {
            iconIv.setImageDrawable(ResourcesCompat.getDrawable(context.resources ,item.image, null))
            descOnb.setPrintableText(item.text)
        }
    }
