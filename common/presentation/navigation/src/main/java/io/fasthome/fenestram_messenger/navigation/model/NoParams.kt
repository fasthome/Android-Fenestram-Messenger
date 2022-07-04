package io.fasthome.fenestram_messenger.navigation.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.ComponentFragmentContractApi
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import kotlinx.parcelize.Parcelize

@Parcelize
object NoParams : Parcelable

fun NavigationContractApi<NoParams, *>.createParams() = createParams(NoParams)

fun ComponentFragmentContractApi<*, NoParams>.createParams() = createParams(NoParams)