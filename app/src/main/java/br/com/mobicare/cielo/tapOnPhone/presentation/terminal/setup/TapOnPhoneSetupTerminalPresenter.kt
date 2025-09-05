package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalRequest
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneTerminalRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class TapOnPhoneSetupTerminalPresenter(
    private val view: TapOnPhoneSetupTerminalContract.View,
    private val userPreferences: UserPreferences,
    private val repository: TapOnPhoneTerminalRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : TapOnPhoneSetupTerminalContract.Presenter {

    private var disposable = CompositeDisposable()
    private var myTerminalInfo: TapOnPhoneTerminalResponse? = null

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onGetUsername(): String = userPreferences.userName

    override fun onGetMyTerminalInfo(): TapOnPhoneTerminalResponse? = myTerminalInfo

    override fun onGetTerminalInfo(
        device: TapOnPhoneTerminalResponse?,
        fingerPrint: String,
        token: String,
        isTransaction: Boolean
    ) {
        checkLoading()

        if (myTerminalInfo == null) {
            myTerminalInfo = device
        }

        if (isTransaction) {
            checkFlow(token, fingerPrint)
        } else {
            callDeviceToken(token, fingerPrint)
        }
    }

    private fun checkLoading(isFinish: Boolean = true) {
        if (isFinish) {
            view.onChangeLoadingText(R.string.tap_on_phone_extension_enable_time)
        } else {
            view.onChangeLoadingText(R.string.tap_on_phone_wait_a_momenty)
        }
    }

    private fun checkFlow(token: String, fingerPrint: String) {
        val terminalInfo = checkTerminalInfo(myTerminalInfo)
        checkLoading(terminalInfo.first)

        if (terminalInfo.first) {
            keepTerminal(terminalInfo.second, terminalInfo.third)
        } else {
            callDeviceToken(token, fingerPrint)
        }
    }

    private fun checkTerminalInfo(terminalInfo: TapOnPhoneTerminalResponse?): Triple<Boolean, String, String> {
        return Triple(
            first = terminalInfo?.deviceId?.isNotBlank() == true && terminalInfo.token?.isNotBlank() == true,
            second = terminalInfo?.deviceId ?: EMPTY,
            third = terminalInfo?.token ?: EMPTY
        )
    }

    private fun callDeviceToken(
        token: String,
        fingerPrint: String
    ) {
        handleDevice(
            source = repository.getTerminalInfo(token, TapOnPhoneTerminalRequest(fingerPrint)),
            fingerPrint = fingerPrint,
            onFinallyAction = {
                checkLoading()
            }
        )
    }

    private fun <T> handleDevice(
        source: Observable<T>,
        fingerPrint: String,
        onFinallyAction: (() -> Unit) = { }
    ) {
        disposable.add(source
            .observeOn(uiScheduler)
            .subscribeOn(ioScheduler)
            .doFinally {
                onFinallyAction.invoke()
            }.subscribe({
                val deviceResponse = it as TapOnPhoneTerminalResponse
                myTerminalInfo = deviceResponse
                if (deviceResponse.registerDeviceRequired == true) {
                    createDeviceTerminal(fingerPrint)
                } else {
                    checkTerminal(deviceResponse)
                }
            }, { error ->
                view.onError(APIUtils.convertToErro(error))
            })
        )
    }

    private fun checkTerminal(deviceResponse: TapOnPhoneTerminalResponse) {
        checkTerminalInfo(deviceResponse).apply {
            when {
                first && deviceResponse.changeToken == true -> changeTerminal(second, third)
                first && deviceResponse.changeToken == false -> keepTerminal(second, third)
                else -> view.onError()

            }
        }
    }

    private fun createDeviceTerminal(fingerPrint: String) {
        handleDevice(
            source = repository.createTerminal(TapOnPhoneTerminalRequest(fingerPrint)),
            fingerPrint = fingerPrint
        )
    }

    private fun changeTerminal(deviceId: String, token: String) {
        view.onInitialize(
            deviceId = deviceId,
            onSuccessAction = {
                view.onActivateTerminal(token, deviceId)
            })
    }

    private fun keepTerminal(deviceId: String, token: String) {
        view.onInitialize(
            deviceId = deviceId,
            onSuccessAction = {
                view.onIsTerminalActive {
                    view.onActivateTerminal(token, deviceId)
                }
            })
    }

    override fun onActivateDevice(
        fingerPrint: String,
        isSaleScreen: Boolean,
        isMakeTransaction: Boolean
    ) {
        disposable.add(
            repository.activateTerminal(
                TapOnPhoneTerminalRequest(fingerPrint)
            ).observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    if (response.isSuccessful)
                        onCheckSawTerminalScreenReady(isSaleScreen, isMakeTransaction)
                    else
                        view.onError(APIUtils.convertToErro(response))
                }, { error ->
                    view.onError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun onCheckSawTerminalScreenReady(isSaleScreen: Boolean, isMakeTransaction: Boolean) {
        when {
            isMakeTransaction -> view.onShowSuccessActivateTerminal()
            userPreferences.isSawTerminalScreenReady() && isSaleScreen -> view.onShowInsertSaleValueScreen()
            else -> view.onShowSuccessActivateTerminal()
        }
    }

    override fun onPause() {
        disposable.dispose()
    }
}