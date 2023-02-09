package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestFilesBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter.RecentFilesAdapter

class ProfileGuestFilesFragment :
    BaseFragment<ProfileGuestFilesState, ProfileGuestFilesEvent>(R.layout.fragment_profile_guest_files) {

    private val binding by fragmentViewBinding(FragmentProfileGuestFilesBinding::bind)

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: ProfileGuestFilesViewModel by viewModel(
        getParamsInterface = ProfileGuestFilesNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val recentFilesAdapter =
        RecentFilesAdapter(onDownloadDocument = { meta, progressListener ->
            vm.onDownloadDocument(meta = meta, progressListener = progressListener)
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        allFilesList.adapter = recentFilesAdapter
        profileGuestFilesAppbar.setNavigationOnClickListener {
            vm.navigateBack()
        }

        val sv = profileGuestFilesAppbar.menu.findItem(R.id.search).actionView as SearchView
        sv.queryHint = getString(R.string.search)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    vm.filterFiles(it)
                }
                return true
            }
        })
    }

    override fun renderState(state: ProfileGuestFilesState) {
        recentFilesAdapter.items = state.files
    }

    override fun handleEvent(event: ProfileGuestFilesEvent) = noEventsExpected()

}