package io.fasthome.fenestram_messenger.presentation.base.ui

import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.requestParams

inline fun <reified I : Any> Fragment.registerFragment(
    componentFragmentContractInterface: ComponentFragmentContractApi<I, NoParams>,
    @IdRes containerViewId: Int? = null,
    tag: String = "${I::class.java.name}@${containerViewId.toString()}",
): Lazy<I> = registerFragment(
    componentFragmentContractInterface = componentFragmentContractInterface,
    paramsProvider = { NoParams },
    containerViewId = containerViewId,
    tag = tag,
)

/**
 * Добавляет вложенный фрагмент, чей результат навигации будет игнорироваться
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified I : Any, P : Parcelable> Fragment.registerFragment(
    componentFragmentContractInterface: ComponentFragmentContractApi<I, P>,
    crossinline paramsProvider: () -> P,
    @IdRes containerViewId: Int? = null,
    tag: String = "${I::class.java.name}@${containerViewId.toString()}",
): Lazy<I> {
    val lazy: Lazy<I> = lazy {
        val fragment = childFragmentManager.findFragmentByTag(tag)
            ?: (componentFragmentContractInterface.createParams(paramsProvider()).createFragment())
                .also { childFragment ->
                    childFragment.requestParams = RequestParams.createWithIgnoreResult()

                    childFragmentManager.commitNow {
                        if (containerViewId != null) {
                            add(containerViewId, childFragment, tag)
                        } else {
                            add(childFragment, tag)
                        }
                    }
                }
        (fragment as InterfaceFragment<I>).getInterface()
    }
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            lazy.value // trigger creating and adding fragment, if not yet.
            lifecycle.removeObserver(this)
        }
    })
    return lazy
}