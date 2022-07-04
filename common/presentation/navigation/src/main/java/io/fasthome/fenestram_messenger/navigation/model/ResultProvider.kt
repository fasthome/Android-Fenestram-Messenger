package io.fasthome.fenestram_messenger.navigation.model

import android.os.Bundle
import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.GetResultInterface

interface ResultProvider {
    fun <R : Parcelable> getResult(getResult: GetResultInterface<R>): R?

    fun <R : Parcelable> consumeResult(
        getResultInterface: GetResultInterface<R>,
        consume: (R) -> Unit,
    ) {
        this.getResult(getResultInterface)
            ?.also(consume)
    }
}

class BundleResultProvider(private val bundle: Bundle) : ResultProvider {
    override fun <R : Parcelable> getResult(getResult: GetResultInterface<R>): R? =
        getResult.parseResult(bundle)
}
