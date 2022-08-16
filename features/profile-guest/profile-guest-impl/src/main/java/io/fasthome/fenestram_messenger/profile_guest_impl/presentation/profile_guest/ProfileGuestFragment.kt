package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentFilesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentImagesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class ProfileGuestFragment :
    BaseFragment<ProfileGuestState, ProfileGuestEvent>(R.layout.fragment_profile_guest) {

    private val binding by fragmentViewBinding(FragmentProfileGuestBinding::bind)
    private val recentFilesAdapter = RecentFilesAdapter()
    private val recentImagesAdapter = RecentImagesAdapter()

    override val vm: ProfileGuestViewModel by viewModel(
        getParamsInterface = ProfileGuestNavigationContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        recentFilesList.adapter = recentFilesAdapter
        recentImagesList.adapter = recentImagesAdapter
        recentImagesList.layoutManager = GridLayoutManager(context, 3)

        recentFilesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

//        vm.fetchFilesAndPhotos()

        recentFilesHeader.recentFilesShowAll.setOnClickListener {
            vm.onShowFilesClicked()
        }
        recentImagesHeader.imagesShowAll.setOnClickListener {
            vm.onShowPhotosClicked()
        }
    }

    override fun renderState(state: ProfileGuestState) {
        when {
            state.recentFiles.size > 3 -> {
                recentFilesAdapter.items = state.recentFiles.take(3)
            }
            state.recentImages.isNotEmpty() -> {
                recentFilesAdapter.items = state.recentFiles
            }
            else -> {
                //TODO Нет недавних файлов
            }
        }

        when {
            state.recentImages.size > 6 -> {
                val items = state.recentImages.take(5) +
                        RecentImagesViewItem(
                            state.recentImages[5].image,
                            state.recentImages.size - 5,
                            true
                        )
                recentImagesAdapter.items = items
            }
            state.recentImages.isNotEmpty() -> {
                recentImagesAdapter.items = state.recentImages
            }
            else -> {
                //TODO Нет недавних изображений
            }
        }

        with(binding) {
            profileGuestName.setPrintableText(state.userName)
            profileGuestNickname.setPrintableText(state.userNickname)
            recentFilesHeader.recentFileCount.setPrintableText(
                PrintableText.PluralResource(
                    R.plurals.file_quantity,
                    state.recentFiles.size,
                    state.recentFiles.size
                )
            )
            recentImagesHeader.recentImagesCount.setPrintableText(
                PrintableText.PluralResource(
                    R.plurals.image_quantity,
                    state.recentImages.size,
                    state.recentImages.size
                )
            )
        }
    }

    override fun handleEvent(event: ProfileGuestEvent) = noEventsExpected()

}