package io.fasthome.fenestram_messenger.presentation.base.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import io.fasthome.fenestram_messenger.presentation.base.R
import io.fasthome.fenestram_messenger.presentation.base.databinding.FragmentBottomSheetWithButtonBinding
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.util.view_action.BottomViewAction
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.view_action.FileSelectorButtonEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.reflect.KClass

open class BottomSheetFragmentWithButton(
    private val contentClazz: KClass<out BaseFragment<*, *>>,
    private val config: Config = Config(Config.Scale.Fullscreen, true),
) : BaseBottomSheetFragment(R.layout.fragment_bottom_sheet_with_button, contentClazz, config) {

    private val binding by fragmentViewBinding(FragmentBottomSheetWithButtonBinding::bind)
    private val bottomViewActions: BottomViewAction<FileSelectorButtonEvent> by inject()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(binding.root, binding.content)

        binding.includeBottomInput.btnAttach.onClick {
            bottomViewActions.sendActionToFragment(
                FileSelectorButtonEvent.AttachEvent
            )
        }

        lifecycleScope.launch {
            bottomViewActions.receiveActionsToBottomView().collectLatest { event ->
                CoroutineScope(Dispatchers.Main).launch {
                    when (event) {
                        is FileSelectorButtonEvent.AttachCountEvent -> {
                            binding.includeBottomInput.btnAttach.isEnabled = event.count > 0
                            if (event.count > 0) {
                                binding.includeBottomInput.btnAttach.text =
                                    getString(R.string.common_attach) + " " + resources.getQuantityString(
                                        R.plurals.image_quantity,
                                        event.count,
                                        event.count
                                    )
                            } else {
                                binding.includeBottomInput.btnAttach.setText(R.string.common_select_image)
                            }
                        }
                        is FileSelectorButtonEvent.SheetCloseEvent -> {
                            this@BottomSheetFragmentWithButton.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        this.onDestroy()
        super.onPause()
    }

}