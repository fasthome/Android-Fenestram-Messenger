package io.fasthome.fenestram_messenger.presentation.base.navigation

import android.net.Uri
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.presentation.base.ui.ActivityResultFragment
import kotlinx.parcelize.Parcelize

object PickFileNavigationContract :
    NavigationContract<PickFileNavigationContract.Params, PickFileNavigationContract.Result>(
        PickFileResultFragment::class
    ) {

    @Parcelize
    data class Params(val mimeType: List<String>) : Parcelable


    @Parcelize
    data class Result(val uri: Uri?) : Parcelable
}

class PickFileResultFragment :
    ActivityResultFragment<PickFileNavigationContract.Params, PickFileNavigationContract.Result, Array<String>, Uri?>() {

    override val navigationContract = PickFileNavigationContract
    override val activityContract: ActivityResultContract<Array<String>, Uri?> = ActivityResultContracts.OpenDocument()

    override val mapParams: (PickFileNavigationContract.Params) -> Array<String> = { it.mimeType.toTypedArray() }
    override val mapResult: (Uri?) -> PickFileNavigationContract.Result =
        { PickFileNavigationContract.Result(uri = it) }
}