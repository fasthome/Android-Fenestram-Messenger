package io.fasthome.fenestram_messenger.navigation.contract

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.Creator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import io.fasthome.fenestram_messenger.navigation.Keys
import io.fasthome.fenestram_messenger.navigation.ModalWindow
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.requestParams
import io.fasthome.fenestram_messenger.util.kotlin.ifOrNull
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.reflect.KClass

@Parcelize
internal data class CreateParams<P : Parcelable>(
    val fragmentClazz: Class<out Fragment>,
    val params: P,
) : CreateParamsInterface {

    override fun createScreen(resultKey: String): Screen {
        val creator = Creator<FragmentFactory, Fragment> {
            createFragment().apply {
                this.requestParams = RequestParams(
                    requestKey = UUID.randomUUID().toString(),
                    resultKey = resultKey
                )
            }
        }

        return FragmentScreen(
            clearContainer = !ModalWindow::class.java.isAssignableFrom(fragmentClazz),
            fragmentCreator = creator,
        )
    }

    override fun createFragment(): Fragment = fragmentClazz.newInstance().apply {
        arguments = bundleOf(Keys.KEY_PARAMS to params)
    }
}

internal data class GetParams<out P : Parcelable>(
    val fragmentClazz: KClass<*>,
) : GetParamsInterface<P> {
    override fun getParams(fragment: Fragment): P =
        checkNotNull(fragment.requireArguments().getParcelable(Keys.KEY_PARAMS)) {
            "Fragment's arguments do NOT contain screen's params!"
        }
}

internal data class CreateResult<R : Parcelable>(
    val fragmentClazz: KClass<*>,
    val result: R,
) : CreateResultInterface {
    override fun createBundle() = bundleOf(
        Keys.KEY_SCREEN to fragmentClazz.java.name,
        Keys.KEY_RESULT to result,
    )
}

internal data class GetResult<out R : Parcelable>(
    val fragmentClazz: KClass<*>,
) : GetResultInterface<R> {
    override fun parseResult(bundle: Bundle): R? =
        ifOrNull(bundle.getString(Keys.KEY_SCREEN) == fragmentClazz.java.name) {
            checkNotNull(bundle.getParcelable(Keys.KEY_RESULT)) {
                "Bundle does NOT contain screen's result"
            }
        }
}