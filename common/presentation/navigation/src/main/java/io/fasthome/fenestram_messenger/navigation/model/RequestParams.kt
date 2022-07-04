package io.fasthome.fenestram_messenger.navigation.model

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.Keys
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Parcelize
data class RequestParams(
    val requestKey: String,
    val resultKey: String,
) : Parcelable {

    companion object {

        fun createWithIgnoreResult() = RequestParams(
            requestKey = UUID.randomUUID().toString(),
            resultKey = ContractRouter.IGNORE_RESULT,
        )
    }
}

var Fragment.requestParams: RequestParams by object : ReadWriteProperty<Fragment, RequestParams> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): RequestParams =
        checkNotNull(thisRef.requireArguments().getParcelable(Keys.KEY_REQUEST_PARAMS)) {
            "Fragment's arguments do NOT contain RequestParams!"
        }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: RequestParams) {
        thisRef.arguments = (thisRef.arguments ?: Bundle()).apply {
            putParcelable(Keys.KEY_REQUEST_PARAMS, value)
        }
    }
}