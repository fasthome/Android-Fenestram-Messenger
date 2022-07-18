package io.fasthome.fenestram_messenger.navigation.contract

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Screen
import io.fasthome.fenestram_messenger.navigation.ContractRouter

interface CreateParamsInterface : Parcelable {
    fun createScreen(resultKey: String = ContractRouter.IGNORE_RESULT): Screen
    fun createFragment(): Fragment
}

fun interface GetParamsInterface<out P> {
    fun getParams(fragment: Fragment): P
}

fun interface CreateResultInterface {
    fun createBundle(): Bundle
}

fun interface GetResultInterface<out R : Parcelable> {
    /**
     * @return result, if [bundle] contains result for this [GetResultInterface], null otherwise.
     */
    fun parseResult(bundle: Bundle): R?
}

/**
 * Interface, that can be used in :some-feature:api module to provide contract for screen.
 * Does not contain methods for getting params and setting result, because they are implementation details.
 */
interface NavigationContractApi<in P : Parcelable, out R : Parcelable> {
    fun createParams(params: P): CreateParamsInterface
    val getResult: GetResultInterface<R>
}

interface NavigationContractInterface<P : Parcelable, R : Parcelable> :
    NavigationContractApi<P, R> {

    val getParams: GetParamsInterface<P>
    fun createResult(result: io.fasthome.fenestram_messenger.profile_api.ProfileFeature.ProfileResult.Success): CreateResultInterface
}

fun <P : Parcelable> errorNavigationContract(error: String) = object : NavigationContractApi<P, Nothing> {
    override fun createParams(params: P): CreateParamsInterface = error(error)
    override val getResult: GetResultInterface<Nothing> get() = error(error)
}