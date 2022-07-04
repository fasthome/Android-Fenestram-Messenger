package io.fasthome.fenestram_messenger.presentation.base.util

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.fasthome.fenestram_messenger.navigation.contract.GetParamsInterface
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.requestParams
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.viewModel
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.reflect.KProperty0

class InterfaceFragmentRegistrator {
    @PublishedApi
    internal val module = module {}

    inline fun <reified I : Any> register(
        interfaceFragmentProperty: KProperty0<I>,
        qualifier: Qualifier? = null,
    ): InterfaceFragmentRegistrator {
        module.single(qualifier = qualifier) { interfaceFragmentProperty.get() }
        return this
    }
}

@PublishedApi
internal var interfaceFragmentModules: List<Module>? = null

/**
 * Компоненты могут содержать вложенные компоненты, которые могут иметь тот же тип.
 * При этом должны создаваться разные экземпляры компонентов.
 *
 * Создание VM для вложенных компонентов происходит рекурсивно.
 *
 * Чтобы не происходило конфликтов при создании VM,
 * текущее значение [interfaceFragmentModules] временно убирается из DI и возвращается обратно после создания VM.
 *
 * Например, в CameraComponentFragment добавляются permissionInterface и locationInterface,
 * а в LocationFragment добавляется ещё один permissionInterface.
 */
@PublishedApi
internal fun <T> Koin.withInterfaceFragmentModules(
    interfaceFragmentRegistrator: InterfaceFragmentRegistrator?,
    action: () -> T,
): T {
    val prevModules = interfaceFragmentModules
    if (prevModules != null) unloadModules(prevModules)

    val modules = listOfNotNull(interfaceFragmentRegistrator?.module)

    interfaceFragmentModules = modules
    loadModules(modules, allowOverride = false)

    val result = action()

    unloadModules(modules)

    interfaceFragmentModules = prevModules
    if (prevModules != null) loadModules(prevModules, allowOverride = false)

    return result
}

inline fun <reified VM : ViewModel, reified P : Parcelable> Fragment.viewModel(
    getParamsInterface: GetParamsInterface<P>,
    interfaceFragmentRegistrator: InterfaceFragmentRegistrator? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<VM> = lazy {
    val koin = getKoin()
    koin.withInterfaceFragmentModules(interfaceFragmentRegistrator) {
        koin.viewModel<VM>(
            owner = { ViewModelOwner.from(this, this) },
        ) {
            val screenParams = listOf(requestParams, getParamsInterface.getParams(this))
            val vmParams = parameters?.invoke()?.values.orEmpty()

            parametersOf(*(screenParams + vmParams).toTypedArray())
        }.value
    }
}

inline fun <reified VM : ViewModel> Fragment.viewModel(
    interfaceFragmentRegistrator: InterfaceFragmentRegistrator? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<VM> = viewModel(
    getParamsInterface = { NoParams },
    interfaceFragmentRegistrator = interfaceFragmentRegistrator,
    parameters = parameters,
)