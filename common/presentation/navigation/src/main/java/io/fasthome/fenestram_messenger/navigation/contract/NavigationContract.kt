package io.fasthome.fenestram_messenger.navigation.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.navigation.contract.*
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import kotlin.reflect.KClass

abstract class NavigationContract<P : Parcelable, R : Parcelable>(
    private val fragmentClazz: KClass<out Fragment>,
) : NavigationContractInterface<P, R> {

    override fun createParams(params: P): CreateParamsInterface =
        CreateParams(fragmentClazz.java, params)

    override val getParams: GetParamsInterface<P> = GetParams(fragmentClazz)

    override fun createResult(result: R): CreateResultInterface =
        CreateResult(fragmentClazz, result)

    override val getResult: GetResultInterface<R> = GetResult(fragmentClazz)
}

class NavigationApiMapper<P : Parcelable, R : Parcelable, PD : Parcelable, RD : Parcelable>(
    private val contract: NavigationContractApi<PD, RD>,
    private val paramsMapper: (P) -> PD,
    private val resultMapper: (RD) -> R,
) : NavigationContractApi<P, R> {

    override fun createParams(params: P) = contract.createParams(paramsMapper(params))

    override val getResult = GetResultInterface { bundle ->
        contract.getResult.parseResult(bundle)?.let(resultMapper::invoke)
    }
}

fun <P : Parcelable, R : Parcelable, PD : Parcelable, RD : Parcelable> NavigationContractApi<PD, RD>.map(
    paramsMapper: (P) -> PD,
    resultMapper: (RD) -> R,
) = NavigationApiMapper(this, paramsMapper, resultMapper)

fun <P : Parcelable, PD : Parcelable> NavigationContractApi<PD, NoResult>.mapParams(
    paramsMapper: (P) -> PD,
) = map(paramsMapper, { result: NoResult -> result })

fun <PD : Parcelable> NavigationContractApi<PD, NoResult>.mapNoParams(
    paramsMapper: (NoParams) -> PD,
) = mapParams<NoParams, PD>(paramsMapper)

fun <R : Parcelable, RD : Parcelable> NavigationContractApi<NoParams, RD>.mapResult(
    resultMapper: (RD) -> R,
) = map({ params: NoParams -> params }, resultMapper)