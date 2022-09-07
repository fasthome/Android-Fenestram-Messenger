package io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.onboarding_impl.databinding.FragmentOnboardingBinding
import io.fasthome.fenestram_messenger.onboarding_impl.presentation.onboarding.Page
import io.fasthome.fenestram_messenger.util.*

class OnboardingAdapter(onItemClicked: (Page) -> Unit) : AsyncListDifferDelegationAdapter<Page>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate(onItemClicked)
    )
)



fun createContactsAdapterDelegate(onItemClicked: (Page) -> Unit) =
    adapterDelegateViewBinding<Page, FragmentOnboardingBinding>(
        FragmentOnboardingBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            descOnb.setPrintableText(item.text)
            iconIv.setBackgroundResource(item.image)

        }
    }
