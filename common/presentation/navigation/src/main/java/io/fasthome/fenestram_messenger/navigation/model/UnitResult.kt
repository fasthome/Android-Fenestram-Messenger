package io.fasthome.fenestram_messenger.navigation.model

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.CreateResultInterface
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractInterface
import kotlinx.parcelize.Parcelize

@Parcelize
object UnitResult : Parcelable

fun NavigationContractInterface<*, UnitResult>.createResult() = createResult(UnitResult)
