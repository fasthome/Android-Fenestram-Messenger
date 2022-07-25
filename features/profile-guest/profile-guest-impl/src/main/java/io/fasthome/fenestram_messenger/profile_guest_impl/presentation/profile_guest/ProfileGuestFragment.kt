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
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.FilesAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.PhotosAdapter

class ProfileGuestFragment :
    BaseFragment<ProfileGuestState, ProfileGuestEvent>(R.layout.fragment_profile_guest) {

    private val binding by fragmentViewBinding(FragmentProfileGuestBinding::bind)
    private val filesAdapter = FilesAdapter()
    private val photosAdapter = PhotosAdapter()

    override val vm: ProfileGuestViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        filesList.adapter = filesAdapter
        photosList.adapter = photosAdapter
        photosList.layoutManager = GridLayoutManager(context, 3)

        filesList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        vm.fetchFilesAndPhotos()
    }

    override fun renderState(state: ProfileGuestState) {
        filesAdapter.items = state.files
        photosAdapter.items = state.photos
        with(binding) {
            when (state.files.size) {
                1 -> filesHeader.fileCount.text = "${state.files.size} файл"
                2, 3, 4 -> filesHeader.fileCount.text = "${state.files.size} файла"
                else -> filesHeader.fileCount.text = "${state.files.size} файлов"
            }
            when (state.photos.size) {
                1 -> photosHeader.photoCount.text = "${state.photos.size} изображение"
                2, 3, 4 -> photosHeader.photoCount.text = "${state.photos.size} изображения"
                else -> photosHeader.photoCount.text = "${state.photos.size} изображений"
            }
        }
    }

    override fun handleEvent(event: ProfileGuestEvent) = noEventsExpected()

}