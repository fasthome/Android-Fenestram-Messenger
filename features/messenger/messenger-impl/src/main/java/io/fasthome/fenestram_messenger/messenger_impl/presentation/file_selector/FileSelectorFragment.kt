/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import android.os.Bundle
import android.view.View
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.component.select_from.SelectFromConversationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentFileSelectorBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class FileSelectorFragment : BaseFragment<FileSelectorState, FileSelectorEvent>(R.layout.fragment_file_selector) {

    override val vm: FileSelectorViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentFileSelectorBinding::bind)

    private val adapterImage = SelectFromConversationAdapter(
        onImageClick = { uri ->
            /**
             * TODO Не использовать Uri, используй UriContent
             */
//            vm.onImageClicked(uri)
        },
        onCheckImage = { image ->

        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        rvImages.adapter = adapterImage
        val layoutManager =
            FlexboxLayoutManager(rvImages.context, FlexDirection.ROW, FlexWrap.WRAP)

        rvImages.layoutManager = layoutManager
        rvImages.itemAnimator = null

        //todo isNestedScrollingEnabled = false позволяет избавиться от проблем скролла ресайкла
        rvImages.isNestedScrollingEnabled = false
        //todo а supportBottomSheetScroll() позволяет избавиться от проблем скролла щита, попробуй их совместить
//        rvImages.supportBottomSheetScroll()
    }

    override fun renderState(state: FileSelectorState) {
        adapterImage.items = state.images
    }

    override fun handleEvent(event: FileSelectorEvent) {

    }

    override fun handleSlideCallback(offset: Float) {
        binding.clActionButtons.alpha = 1 - offset
    }

}