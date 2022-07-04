package io.fasthome.fenestram_messenger.navigation.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

interface ComponentFragmentContractApi<I : Any, P : Parcelable> {
    fun createParams(params: P): CreateParamsInterface
}

interface ComponentFragmentContractInterface<I : Any, P : Parcelable> : ComponentFragmentContractApi<I, P> {
    val getParams: GetParamsInterface<P>
}

inline fun <I : Any, P : Parcelable, reified F> ComponentFragmentContract()
        : ComponentFragmentContractInterface<I, P>
        where F : Fragment, F : InterfaceFragment<I> =
    ComponentFragmentContract(F::class)

@PublishedApi
internal class ComponentFragmentContract<I : Any, P : Parcelable>(
    private val fragmentClazz: KClass<out Fragment>,
) : ComponentFragmentContractInterface<I, P> {

    override fun createParams(params: P): CreateParamsInterface = CreateParams(fragmentClazz.java, params)

    override val getParams: GetParamsInterface<P> = GetParams(fragmentClazz)
}