package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.presentation.base.R
import io.fasthome.fenestram_messenger.presentation.base.databinding.FragmentBottomSheetBinding
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import kotlin.reflect.KClass

open class BottomSheetFragment(
    private val contentClazz: KClass<out BaseFragment<*, *>>,
    private val config: Config = Config(),
) : BaseBottomSheetFragment(R.layout.fragment_bottom_sheet, contentClazz, config) {

    private val binding by fragmentViewBinding(FragmentBottomSheetBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated(binding.root, binding.content)
    }

}