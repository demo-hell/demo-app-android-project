package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneSaleInfoModel
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneBaseView

interface TapOnPhoneSetupTerminalContract {

    interface Router {
        fun onPause()
        fun onResume()

        fun onInitializeAllowMe(onAction: () -> Unit, isShowLoading: Boolean = true)

        fun onCheckDeviceCompatibility(
            onActionNFCEnabled: (() -> Unit)? = null,
            isEnabledNFCRequired: Boolean = true,
            gaFlowDetails: String = EMPTY
        )

        fun onInitializeTapOnPhone(
            isCheckDevice: Boolean = true,
            isTransaction: Boolean = true,
            deviceResponse: TapOnPhoneTerminalResponse? = null
        )

        fun onMakeTransaction(transactionInfo: TapOnPhoneSaleInfoModel)
    }

    interface View : TapOnPhoneBaseView {
        fun onInitialize(deviceId: String, onSuccessAction: () -> Unit)
        fun onIsTerminalActive(onActiveAction: () -> Unit)

        fun onActivateTerminal(token: String, deviceId: String)
        fun onShowSuccessActivateTerminal()
        fun onShowInsertSaleValueScreen()
        fun onError(errorMessage: ErrorMessage? = null)
    }

    interface Presenter {
        fun onGetUsername(): String
        fun onCheckSawTerminalScreenReady(isSaleScreen: Boolean, isMakeTransaction: Boolean)

        fun onGetMyTerminalInfo(): TapOnPhoneTerminalResponse?

        fun onGetTerminalInfo(
            device: TapOnPhoneTerminalResponse? = null,
            fingerPrint: String,
            token: String,
            isTransaction: Boolean = false
        )

        fun onActivateDevice(
            fingerPrint: String,
            isSaleScreen: Boolean,
            isMakeTransaction: Boolean
        )

        fun onPause()
        fun onResume()
    }

    interface Result {
        fun getActivityTapOnPhone(): FragmentActivity
        fun getFragmentManagerTapOnPhone(): FragmentManager
        fun isSaleScreen(): Boolean = true
        fun hasCardReader(): Boolean = false

        fun isMakeTransaction(): Boolean = false

        fun onDeviceCompatibility() {}
        fun onNFCIsNotSupported(gaFlowDetails: String) {}
        fun onAndroidIsNotSupported(gaFlowDetails: String) {}

        fun onEnableNFC(device: TapOnPhoneTerminalResponse?)

        fun onDevelopModeEnable(device: TapOnPhoneTerminalResponse?) {}

        fun onSuccessInActivatingTerminal(device: TapOnPhoneTerminalResponse?) {}
        fun onShowInsertSaleValueScreen(device: TapOnPhoneTerminalResponse?) {}
        fun onErrorInActivatingTerminal(
            errorCode: Short? = null,
            errorMessage: ErrorMessage? = null
        ) {
        }

        fun onTapChangeLoadingText(@StringRes message: Int? = null) {}
        fun onTapShowLoading(@StringRes message: Int? = null) {}
        fun onTapHideLoading() {}

        fun onTransactionFailedError(errorCode: Short) {}
        fun onExtensionError() {}
        fun onConnectCardReaderError() {}
        fun onRetryConnectCardReader(device: TapOnPhoneTerminalResponse?) {}
        fun onMakeTransactionErrorWithCodeFortyTwo() {}
    }
}