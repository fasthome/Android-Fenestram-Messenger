package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestFilesBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.adapter.AllFilesAdapter

class ProfileGuestFilesFragment :
    BaseFragment<ProfileGuestFilesState, ProfileGuestFilesEvent>(R.layout.fragment_profile_guest_files) {

    private val binding by fragmentViewBinding(FragmentProfileGuestFilesBinding::bind)
    private val allFilesAdapter = AllFilesAdapter()

    override val vm: ProfileGuestFilesViewModel by viewModel(getParamsInterface = ProfileGuestFilesNavigationContract.getParams)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        allFilesList.adapter = allFilesAdapter

        profileGuestFilesAppbar.setNavigationOnClickListener {
            vm.navigateBack()
        }

        profileGuestFilesAppbar.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    Toast.makeText(context, "Search", Toast.LENGTH_SHORT)
                    true
                }
                else -> {
                    false
                }
            }

        }

        vm.fetchFiles()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
//        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchView.clearFocus();
//                /*   if(list.contains(query)){
//                       adapter.getFilter().filter(query);
//                   }else{
//                       Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
//                   }*/
//                return false;
//
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
        }

    override fun renderState(state: ProfileGuestFilesState) {
        allFilesAdapter.items = state.files
    }

    override fun handleEvent(event: ProfileGuestFilesEvent) = noEventsExpected()

}