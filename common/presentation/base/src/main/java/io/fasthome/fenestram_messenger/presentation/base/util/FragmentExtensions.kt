package io.fasthome.fenestram_messenger.presentation.base.util

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.BackPressConsumer
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.util.doOnDestroy
import io.fasthome.fenestram_messenger.util.getPrintableText
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("unused")
fun BaseFragment<*, *>.nothingToRender() = Unit

@Suppress("unused")
fun BaseFragment<*, *>.noEventsExpected(): Nothing =
    error("No events expected! Add implementation if needed.")

typealias ViewBindingInflate<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

private val mainHandler = Handler(Looper.getMainLooper())

fun <B : ViewBinding> Fragment.fragmentViewBinding(
    bindingCreator: (View) -> B,
) = object : ReadOnlyProperty<Fragment, B> {
    private var binding: B? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): B {
        //view может быть null, если обращение к binding в onDestroyView
        this.binding
            ?.takeIf { view == null || it.root == view }
            ?.let { return it }

        val newBinding = bindingCreator(requireView())
        binding = newBinding
        viewLifecycleOwner.lifecycle.doOnDestroy {
            mainHandler.post {
                if (binding === newBinding) {
                    binding = null
                }
            }
        }

        return newBinding
    }
}

fun <B : ViewBinding> Fragment.nestedViewBinding(
    nestedContentRoot: () -> ViewGroup,
    inflate: ViewBindingInflate<B>,
) = object : ReadOnlyProperty<Fragment, B> {
    private var binding: B? = null
    @SuppressLint("StaticFieldLeak")
    private var nestedRoot: ViewGroup? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): B {
        //view может быть null, если обращение к binding в onDestroyView
        this.binding
            ?.takeIf { view == null || nestedRoot == nestedContentRoot() }
            ?.let { return it }

        val nestedRoot = nestedContentRoot()
        val newBinding = inflate(layoutInflater, nestedRoot, true)
        this.nestedRoot = nestedRoot
        binding = newBinding
        viewLifecycleOwner.lifecycle.doOnDestroy {
            mainHandler.post {
                if (binding === newBinding) {
                    binding = null
                    this.nestedRoot = null
                }
            }
        }

        return newBinding
    }
}

internal fun FragmentManager.onBackPressed(): Boolean =
    fragments.any { it is BackPressConsumer && it.onBackPressed() }

fun Fragment.showMessage(message: Message): Unit = when (message) {
    is Message.Alert -> {
//        val tag = message.id
//        (childFragmentManager.findFragmentByTag(tag) as MessageDialogFragment?)
//            ?.dismissAllowingStateLoss()
//        MessageDialogFragment
//            .create(requestParams.requestKey, message)
//            .show(childFragmentManager, tag)
    }

    is Message.PopUp -> Toast
        .makeText(requireContext(), getPrintableText(message.messageText), Toast.LENGTH_LONG)
        .show()
}