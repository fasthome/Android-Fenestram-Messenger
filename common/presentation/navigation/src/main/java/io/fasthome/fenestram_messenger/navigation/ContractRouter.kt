package io.fasthome.fenestram_messenger.navigation

import android.os.Bundle
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import io.fasthome.fenestram_messenger.navigation.contract.CreateParamsInterface
import io.fasthome.fenestram_messenger.navigation.contract.CreateResultInterface
import io.fasthome.fenestram_messenger.navigation.model.BundleResultProvider
import io.fasthome.fenestram_messenger.navigation.model.CloseModalScreens
import io.fasthome.fenestram_messenger.navigation.model.ResultProvider
import kotlinx.coroutines.flow.*

typealias CustomCicerone = Cicerone<ContractRouterImpl>

interface ContractRouter {

    fun observeResult(resultKey: String): Flow<ResultProvider>

    fun exit()

    fun exitWithResult(
        resultKey: String,
        result: CreateResultInterface,
    )

    fun backTo(createParamsInterface: CreateParamsInterface?)

    fun finishChain()

    fun navigateTo(
        createParamsInterface: CreateParamsInterface,
        targetRequestKey: String,
    )

    fun newChain(vararg createParamsInterfaces: CreateParamsInterface)

    fun newRootChain(vararg createParamsInterfaces: CreateParamsInterface)

    fun newRootScreen(createParamsInterface: CreateParamsInterface)

    fun replaceScreen(createParamsInterface: CreateParamsInterface)

    fun closeModalScreens()

    companion object {
        const val IGNORE_RESULT = "ignoreResult"
    }
}

@Suppress("UNUSED")
open class ContractRouterImpl : Router(), ContractRouter {

    private val resultStore = MutableStateFlow<Map<String, Bundle>>(mapOf())

    override fun observeResult(resultKey: String): Flow<ResultProvider> =
        resultStore
            .mapNotNull { store -> store[resultKey] }
            .onEach { updateResult { minus(resultKey) } }
            .map { BundleResultProvider(it) }

    override fun exitWithResult(resultKey: String, result: CreateResultInterface) {
        super.exit()

        if (resultKey != ContractRouter.IGNORE_RESULT) {
            updateResult { plus(resultKey to result.createBundle()) }
        }
    }

    private fun updateResult(block: Map<String, Bundle>.() -> Map<String, Bundle>) {
        resultStore.value = resultStore.value.block()
    }

    override fun backTo(createParamsInterface: CreateParamsInterface?) {
        super.backTo(createParamsInterface?.createScreen())
    }

    override fun navigateTo(
        createParamsInterface: CreateParamsInterface,
        targetRequestKey: String,
    ) {
        super.navigateTo(createParamsInterface.createScreen(targetRequestKey))
    }

    override fun newChain(
        vararg createParamsInterfaces: CreateParamsInterface,
    ) {
        super.newChain(*createParamsInterfaces.map { it.createScreen() }.toTypedArray())
    }

    override fun newRootChain(
        vararg createParamsInterfaces: CreateParamsInterface,
    ) {
        super.newRootChain(*createParamsInterfaces.map { it.createScreen() }.toTypedArray())
    }

    override fun newRootScreen(createParamsInterface: CreateParamsInterface) {
        super.newRootScreen(createParamsInterface.createScreen())
    }

    override fun replaceScreen(createParamsInterface: CreateParamsInterface) {
        super.replaceScreen(createParamsInterface.createScreen())
    }

    override fun closeModalScreens() {
        executeCommands(CloseModalScreens)
    }
}