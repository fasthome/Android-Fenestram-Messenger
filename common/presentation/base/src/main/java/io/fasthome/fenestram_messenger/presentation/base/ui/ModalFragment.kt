package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.ModalWindow
import io.fasthome.fenestram_messenger.presentation.base.R
import io.fasthome.fenestram_messenger.presentation.base.databinding.FragmentBaseModalBinding
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.onBackPressed
import org.koin.android.ext.android.inject
import kotlin.reflect.KClass

open class ModalFragment(
    private val contentClazz: KClass<out Fragment>,
) : Fragment(R.layout.fragment_base_modal), ModalWindow, BackPressConsumer {

    private companion object {
        const val CONTENT_TAG = "CONTENT_FRAGMENT"
    }

    private val router: ContractRouter by inject()
    private val binding by fragmentViewBinding(FragmentBaseModalBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentByTag(CONTENT_TAG) == null) {
            childFragmentManager.commitNow {
                add(R.id.content, contentClazz.java, arguments, CONTENT_TAG)
            }
        }
    }

    override fun onBackPressed(): Boolean =
        childFragmentManager.onBackPressed()
}