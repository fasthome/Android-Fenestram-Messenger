package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestImagesBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.adapter.ImagesAdapter
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.getSpanCount

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
        val gridLayoutManager = GridLayoutManager(
            requireContext(), resources.getSpanCount(
                resources.getDimension(R.dimen.min_image_height).toInt()
            )
        )
        allImagesList.layoutManager = gridLayoutManager

        profileGuestImagesAppbar.setNavigationOnClickListener {
            vm.navigateBack()
        }
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        binding.profileGuestImagesAppbar.setTitleTextColor(appTheme.text0Color())
        binding.profileGuestImagesAppbar.setNavigationIconTint(appTheme.text0Color())
        binding.profileGuestImagesAppbar.backgroundTintList = ColorStateList.valueOf(appTheme.bg3Color())
    }

    override fun renderState(state: ProfileGuestImagesState) {
        imagesAdapter.items = state.images
    }

    override fun handleEvent(event: ProfileGuestImagesEvent) = noEventsExpected()


}