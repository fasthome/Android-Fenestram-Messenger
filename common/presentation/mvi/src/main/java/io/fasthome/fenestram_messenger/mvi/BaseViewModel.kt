package io.fasthome.fenestram_messenger.mvi

import android.os.Parcelable
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.contract.CreateResultInterface
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.UnitResult
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.kotlin.Activable
import io.fasthome.fenestram_messenger.util.kotlin.activableFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<S : Any, E : Any>(
    protected val router: ContractRouter,
    private val requestParams: RequestParams,
) : ViewModel(), ViewModelInterface<S, BaseViewEvent<E>> {

    ////////////////////// State //////////////////////////

    private val _viewState by lazy { MutableStateFlow(createInitialState()) }
    override val viewState get() = _viewState.asStateFlow()

    private val _viewEvent = Channel<BaseViewEvent<E>>(Channel.UNLIMITED)
    override val viewEvent = _viewEvent.receiveAsFlow()

    protected val currentViewState: S get() = viewState.value

    protected abstract fun createInitialState(): S

    protected fun updateState(update: (S) -> S) {
        _viewState.update(update)
    }

    protected fun sendEvent(event: BaseViewEvent<E>) {
        _viewEvent.trySend(event)
    }

    protected fun sendEvent(event: E) {
        sendEvent(BaseViewEvent.ScreenEvent(event))
    }

    protected interface LoadController {
        fun update()
    }


    //////////////////// Lifecycle ////////////////////////

    /**
     * @return true, if consumed.
     */
    override fun onBackPressed(): Boolean = false

    private val viewActivable = Activable()

    @CallSuper
    override fun onViewActive() {
        viewActivable.onActive()
    }

    @CallSuper
    override fun onViewInactive() {
        viewActivable.onInactive()
    }

    @CallSuper
    override fun onCreate() = Unit

    protected fun <T> Flow<T>.collectWhenViewActive() =
        activableFlow(originalFlow = this, activable = viewActivable, scope = viewModelScope)

    //////////////////// Messages ////////////////////////

    @CallSuper
    override fun onMessageResult(result: MessageResult) {
        if (result.id != Message.ID_NO_MATTER) {
            error("Override this method to handle MessageResult!")
        }
    }

    protected open val errorConverter: ErrorConverter = DefaultErrorConverter

    protected inline fun <T> CallResult<T>.withErrorHandled(
        showErrorType: ShowErrorType = ShowErrorType.Popup,
        onSuccess: (T) -> Unit,
    ): Unit = when (this) {
        is CallResult.Error -> onError(showErrorType, error)
        is CallResult.Success -> onSuccess(this.data)
    }

    protected fun <T> CallResult<T>.successOrSendError(
        showErrorType: ShowErrorType = ShowErrorType.Popup,
    ): T? = when (this) {
        is CallResult.Error -> {
            onError(showErrorType, error)
            null
        }
        is CallResult.Success -> this.data
    }

    protected fun showMessage(message: Message) {
        sendEvent(BaseViewEvent.ShowMessage(message))
    }

    protected fun showPopup(printableText: PrintableText) {
        showMessage(Message.PopUp(messageText = printableText))
    }

    protected fun showAlert(
        messageText: PrintableText,
        id: String = Message.ID_NO_MATTER,
        titleText: PrintableText? = null,
        actionText: PrintableText? = null,
    ) {
        showMessage(
            Message.Alert(
                id = id,
                messageText = messageText,
                titleText = titleText,
                actionText = actionText,
            )
        )
    }

    protected fun showDialog(
        titleText: PrintableText,
        messageText: PrintableText,
        onCloseClick: () -> Unit,
        onRetryClick: () -> Unit
    ) {
        sendEvent(
            BaseViewEvent.ShowDialog(
                Message.Dialog(messageText = messageText, titleText = titleText),
                onCloseClick,
                onRetryClick
            )
        )
    }

    fun onError(
        showErrorType: ShowErrorType,
        throwable: Throwable,
        onCloseClick: () -> Unit = {},
        onRetryClick: () -> Unit = {}
    ) {
        val errorInfo = errorConverter.convert(throwable)

        when (showErrorType) {
            ShowErrorType.Popup -> showPopup(errorInfo.description)
            ShowErrorType.Alert -> showAlert(
                titleText = errorInfo.title,
                messageText = errorInfo.description,
            )
            ShowErrorType.Dialog -> showDialog(
                titleText = errorInfo.title,
                messageText = errorInfo.description,
                onCloseClick,
                onRetryClick
            )
        }
    }

    protected fun exitWithResult(createResultInterface: CreateResultInterface) {
        router.exitWithResult(requestParams.resultKey, createResultInterface)
    }

    protected fun exitWithoutResult() {
        router.exit()
    }

    private val screens = mutableListOf<ScreenLauncherImpl<*, *>>()

    init {
        @Suppress("UNCHECKED_CAST")
        router.observeResult(requestParams.requestKey)
            .onEach {
                screens.forEach { s ->
                    it.consumeResult(
                        getResultInterface = s.navigationContractApi.getResult,
                        consume = s.consumeResult as (Parcelable) -> Unit,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    protected fun <P : Parcelable, R : Parcelable> registerScreen(
        navigationContractApi: NavigationContractApi<P, R>,
        consumeResult: (R) -> Unit,
    ): ScreenLauncher<P> =
        ScreenLauncherImpl(router, requestParams.requestKey, navigationContractApi, consumeResult)
            .also { screens.add(it) }

    @JvmName("registerScreenNoResult")
    protected fun <P : Parcelable> registerScreen(
        navigationContractApi: NavigationContractApi<P, NoResult>,
    ): ScreenLauncher<P> = registerScreen(
        navigationContractApi = navigationContractApi,
        consumeResult = {},
    )

    @JvmName("registerScreenUnitResult")
    protected fun <P : Parcelable> registerScreen(
        navigationContractApi: NavigationContractApi<P, UnitResult>,
    ): ScreenLauncher<P> = registerScreen(
        navigationContractApi = navigationContractApi,
        consumeResult = {},
    )

    protected fun ScreenLauncher<NoParams>.launch() {
        launch(NoParams)
    }
}