package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentOnboardingBinding
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentViewpager2Binding
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.Page
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.PageLists
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
            iconIv.setBackgroundResource(item.image)
            descOnb.setPrintableText(item.text)
        }
    }
