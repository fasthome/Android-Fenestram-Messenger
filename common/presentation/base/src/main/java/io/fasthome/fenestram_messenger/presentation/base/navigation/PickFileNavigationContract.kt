package io.fasthome.fenestram_messenger.presentation.base.navigation

import android.net.Uri
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContracts
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.presentation.base.ui.ActivityResultFragment
import kotlinx.parcelize.Parcelize

object PickFileNavigationContract :
    NavigationContract<PickFileNavigationContract.Params, PickFileNavigationContract.Result>(
        PickFileResultFragment::class
    ) {

    @Parcelize
    data class Params(val mimeType: String) : Parcelable


    @Parcelize
    data class Result(val uri: Uri?) : Parcelable
}

class PickFileResultFragment :
    ActivityResultFragment<PickFileNavigationContract.Params, PickFileNavigationContract.Result, String, Uri?>() {

    override val navigationContract = PickFileNavigationContract
    override val activityContract = ActivityResultContracts.GetContent()

    override val mapParams: (PickFileNavigationContract.Params) -> String = { it.mimeType }
    override val mapResult: (Uri?) -> PickFileNavigationContract.Result =
        { PickFileNavigationContract.Result(uri = it) }
}