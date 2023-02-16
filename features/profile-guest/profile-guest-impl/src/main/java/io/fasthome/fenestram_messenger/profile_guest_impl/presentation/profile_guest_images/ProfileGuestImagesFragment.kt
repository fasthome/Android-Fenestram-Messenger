package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import android.os.Bundle
import android.view.View
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestImagesBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.adapter.ImagesAdapter

class ProfileGuestImagesFragment :
    BaseFragment<ProfileGuestImagesState, ProfileGuestImagesEvent>(R.layout.fragment_profile_guest_images) {

    private val binding by fragmentViewBinding(FragmentProfileGuestImagesBinding::bind)

    private val imagesAdapter = ImagesAdapter(
        onItemClicked = {
            vm.onItemClicked(it)
        }
    )

    override val vm: ProfileGuestImagesViewModel by viewModel(getParamsInterface = ProfileGuestImagesNavigationContract.getParams)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        allImagesList.adapter = imagesAdapter
        val flexboxManager =
            FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
        allImagesList.layoutManager = flexboxManager

        profileGuestImagesAppbar.setNavigationOnClickListener {
            vm.navigateBack()
        }
    }

    override fun renderState(state: ProfileGuestImagesState) {
        imagesAdapter.items = state.images
    }

    override fun handleEvent(event: ProfileGuestImagesEvent) = noEventsExpected()

}