package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.FORMAT_TIME_CIELO_TAP
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_24h
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.isDevMode
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.extensions.errorAllowMe
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.isAvailable
import br.com.mobicare.cielo.tapOnPhone.constants.COUNTRY_CODE
import br.com.mobicare.cielo.tapOnPhone.domain.model.DynamicBankData
import br.com.mobicare.cielo.tapOnPhone.domain.model.ReceiptInfo
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneSaleInfoModel
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.domain.model.TransactionReceiptData
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.CARD_READER_NOT_CONNECT
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants.ERROR_CODE_FORTY_TWO_IN_MAKE_TRANSACTION
import com.google.gson.Gson
import com.symbiotic.taponphone.Data.PaymentData
import com.symbiotic.taponphone.Enums.PaymentStatus
import com.symbiotic.taponphone.Enums.ReaderType
import com.symbiotic.taponphone.Interfaces.IsTerminalActiveListener
import com.symbiotic.taponphone.Interfaces.PaymentListener
import com.symbiotic.taponphone.Interfaces.ReaderConnectionListener
import com.symbiotic.taponphone.TapOnPhone
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TapOnPhoneSetupTerminal(
    private val resultListener: TapOnPhoneSetupTerminalContract.Result,
    private val datadogEvent: DatadogEvent
) : TapOnPhoneSetupTerminalContract.View,
    TapOnPhoneSetupTerminalContract.Router,
    AllowMeContract.View,
    KoinComponent {

    private val presenter: TapOnPhoneSetupTerminalPresenter by inject {
        parametersOf(this@TapOnPhoneSetupTerminal)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this@TapOnPhoneSetupTerminal)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(fragmentManager)
    }

    private var _fingerPrint: String = EMPTY
    private val fingerPrint get() = _fingerPrint

    private var tapInstance: TapOnPhone? = null
    private var isCardReader = resultListener.hasCardReader()

    private val activity get() = resultListener.getActivityTapOnPhone()

    private val isSaleScreen get() = resultListener.isSaleScreen()
    private val isMakeTransaction get() = resultListener.isMakeTransaction()
    private val fragmentManager get() = resultListener.getFragmentManagerTapOnPhone()

    private var onAllowMeAction: (() -> Unit)? = null

    init {
        onResume()
    }

    override fun onResume() {
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
    }

    override fun onInitializeAllowMe(onAction: () -> Unit, isShowLoading: Boolean) {
        onAllowMeAction = onAction
        if (isShowLoading)
            onShowLoading(R.string.tap_on_phone_wait_a_momenty)

        allowMePresenter.collect(
            mAllowMeContextual = allowMePresenter.init(activity),
            context = activity,
            mandatory = true
        )
    }

    override fun onCheckDeviceCompatibility(
        onActionNFCEnabled: (() -> Unit)?,
        isEnabledNFCRequired: Boolean,
        gaFlowDetails: String
    ) {
        if (SDK_INT >= Build.VERSION_CODES.Q) {
            if (activity.isDevMode()) {
                developModeEnable()
            } else {
                checkNFC(
                    onActionNFCEnabled = {
                        onActionNFCEnabled?.invoke() ?: run {
                            resultListener.onDeviceCompatibility()
                        }
                    },
                    isEnabledNFCRequired,
                    gaFlowDetails
                )
            }

        } else {
            androidIsNotSupported(gaFlowDetails)
        }
    }

    private fun developModeEnable() {
        resultListener.onTapHideLoading()
        resultListener.onDevelopModeEnable(presenter.onGetMyTerminalInfo())
    }

    private fun androidIsNotSupported(gaFlowDetails: String) {
        resultListener.onTapHideLoading()
        resultListener.onAndroidIsNotSupported(gaFlowDetails)
    }

    private fun checkNFC(
        onActionNFCEnabled: () -> Unit,
        isEnableRequired: Boolean = true,
        gaFlowDetails: String
    ) {
        nfcAdapter()?.let { nfc ->
            if (nfc.isEnabled || isEnableRequired.not())
                onActionNFCEnabled.invoke()
            else {
                resultListener.onTapHideLoading()
                resultListener.onEnableNFC(presenter.onGetMyTerminalInfo())
            }
        } ?: run {
            if (isCardReader) {
                onActionNFCEnabled.invoke()
            } else {
                resultListener.onTapHideLoading()
                resultListener.onNFCIsNotSupported(gaFlowDetails)
            }
        }
    }

    private fun nfcAdapter(): NfcAdapter? {
        val manager = activity.getSystemService(Context.NFC_SERVICE) as? NfcManager
        return manager?.defaultAdapter
    }

    override fun onInitializeTapOnPhone(
        isCheckDevice: Boolean,
        isTransaction: Boolean,
        deviceResponse: TapOnPhoneTerminalResponse?
    ) {
        if (isCheckDevice) {
            onShowLoading(R.string.tap_on_phone_wait_a_momenty)
            onCheckDeviceCompatibility(onActionNFCEnabled = {
                validationTokenWrapper.generateOtp(
                    onResult = { otpCode ->
                        presenter.onGetTerminalInfo(
                            device = deviceResponse,
                            fingerPrint = fingerPrint,
                            token = otpCode,
                            isTransaction = isTransaction
                        )
                    }
                )
            })
        } else
            validationTokenWrapper.generateOtp(
                onResult = { otpCode ->
                    presenter.onGetTerminalInfo(
                        device = deviceResponse,
                        fingerPrint = fingerPrint,
                        token = otpCode,
                        isTransaction = isTransaction
                    )
                }
            )
    }

    override fun successCollectToken(result: String) {
        _fingerPrint = result
        onAllowMeAction?.invoke()
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        activity.errorAllowMe(
            isMandatory = mandatory,
            message = errorMessage,
            onMandatoryAction = {
                resultListener.onTapHideLoading()
            }, onNotMandatoryAction = {
                _fingerPrint = result ?: EMPTY
                onAllowMeAction?.invoke()
            }
        )
    }

    override fun getSupportFragmentManagerInstance() = fragmentManager

    override fun isAttached() = activity.isAvailable()

    override fun onChangeLoadingText(message: Int?) {
        resultListener.onTapChangeLoadingText(message)
    }

    override fun onShowLoading(message: Int?) {
        resultListener.onTapShowLoading(message)
    }

    override fun onHideLoading() {
        resultListener.onTapHideLoading()
    }

    override fun onInitialize(
        deviceId: String,
        onSuccessAction: () -> Unit
    ) {
        TapOnPhone.setTestMode(BuildConfig.TAP_TEST_MODE)
        val isInitializeTerminal = TapOnPhone.initialize(
            activity.applicationContext,
            COUNTRY_CODE,
            BuildConfig.TAP_KEY,
            deviceId
        )
        if (isInitializeTerminal) {
            TapOnPhone.getInstance().setEnvironment(BuildConfig.TAP_ENVIROMENT, false)
            tapInstance = TapOnPhone.getInstance()
            onSuccessAction.invoke()
        } else {
            datadogEvent.LoggerInfo(
                message = "Cielo TAP: Inicialização",
                key = "cieloTapInformation",
                value = "Terminal não foi inicializado"
            )
            showErrorSDK()
        }
    }

    override fun onIsTerminalActive(onActiveAction: () -> Unit) {
        tapInstance?.isTerminalActive(object : IsTerminalActiveListener {
            override fun onIsTerminalActiveResult(result: Boolean) {
                datadogEvent.LoggerInfo(
                    message = "Cielo TAP: verificação do terminal",
                    key = "cieloTapInformation",
                    value = "Resultado da verificação de ativação do terminal: $result"
                )
                when {
                    result && isCardReader -> {
                        datadogEvent.LoggerInfo(
                            message = "Cielo TAP: verificação do terminal",
                            key = "cieloTapInformation",
                            value = "Terminal ativo e cliente tem card reader contratado"
                        )
                        connectReader(
                            onInitialized = {
                                presenter.onCheckSawTerminalScreenReady(
                                    isSaleScreen,
                                    isMakeTransaction
                                )
                            }
                        )
                    }

                    result && isCardReader.not() -> {
                        datadogEvent.LoggerInfo(
                            message = "Cielo TAP: verificação do terminal",
                            key = "cieloTapInformation",
                            value = "Terminal ativo e cliente sem card reader contratado"
                        )
                        presenter.onCheckSawTerminalScreenReady(
                            isSaleScreen,
                            isMakeTransaction
                        )
                    }

                    else -> {
                        datadogEvent.LoggerInfo(
                            message = "Cielo TAP: verificação do terminal",
                            key = "cieloTapInformation",
                            value = "Terminal não estava ativo"
                        )
                        onActiveAction()
                    }
                }
            }

            override fun onIsTerminalActiveError() {
                datadogEvent.LoggerInfo(
                    message = "Cielo TAP: verificação do terminal",
                    key = "cieloTapInformation",
                    value = "Falha na ativação do terminal symbiotic"
                )
                showErrorSDK()
            }
        }) ?: showError()
    }

    override fun onActivateTerminal(token: String, deviceId: String) {
        tapInstance?.activate(token) { result, error ->
            datadogEvent.LoggerInfo(
                message = "Cielo TAP: ativação do terminal",
                key = "cieloTapInformation",
                value = "Resultado da ativação do terminal: $result"
            )
            when {
                result && isCardReader -> {
                    datadogEvent.LoggerInfo(
                        message = "Cielo TAP: ativação do terminal",
                        key = "cieloTapInformation",
                        value = "Sucesso na ativação do terminal e cliente tem card reader contratado"
                    )
                    connectReader(
                        onInitialized = {
                            presenter.onActivateDevice(
                                fingerPrint,
                                isSaleScreen,
                                isMakeTransaction
                            )
                        }
                    )
                }

                result && isCardReader.not() -> {
                    datadogEvent.LoggerInfo(
                        message = "Cielo TAP: ativação do terminal",
                        key = "cieloTapInformation",
                        value = "Sucesso na ativação do terminal e cliente sem card reader contratado"
                    )
                    presenter.onActivateDevice(
                        fingerPrint,
                        isSaleScreen,
                        isMakeTransaction
                    )
                }

                else -> {
                    datadogEvent.LoggerInfo(
                        message = "Cielo TAP: ativação do terminal",
                        key = "cieloTapInformation",
                        value = "Falha na ativação do terminal errorcode: $error"
                    )
                    showErrorSDK()
                }
            }
        } ?: showError()
    }

    override fun onShowSuccessActivateTerminal() {
        resultListener.onTapHideLoading()
        resultListener.onSuccessInActivatingTerminal(presenter.onGetMyTerminalInfo())
    }

    override fun onShowInsertSaleValueScreen() {
        resultListener.onTapHideLoading()
        resultListener.onShowInsertSaleValueScreen(presenter.onGetMyTerminalInfo())
    }

    override fun onError(errorMessage: ErrorMessage?) {
        showError(errorMessage = errorMessage)
    }

    private fun showError(errorMessage: ErrorMessage? = null) {
        resultListener.onTapHideLoading()
        resultListener.onErrorInActivatingTerminal(errorMessage = errorMessage)
    }

    private fun showErrorSDK() {
        resultListener.onTapHideLoading()
        resultListener.onExtensionError()
    }

    private fun connectReader(onInitialized: () -> Unit) {
        tapInstance?.connectReader(
            ReaderType.MPOS_D140, object : ReaderConnectionListener {
                override fun onConnectionResult(p0: Boolean, p1: Int) {
                    onHideLoading()
                    when {
                        p0 -> {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: conexão card reader",
                                key = "cieloTapInformation",
                                value = "Sucesso na conexão do card reader"
                            )
                            onInitialized()
                        }

                        p1 == CARD_READER_NOT_CONNECT && nfcAdapter()?.isEnabled == true -> {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: conexão card reader",
                                key = "cieloTapInformation",
                                value = "Máquina não conectada no dispositivo e dispositivo com NFC"
                            )
                            onInitialized()
                        }

                        p1 == CARD_READER_NOT_CONNECT && nfcAdapter()?.isEnabled == false -> {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: conexão card reader",
                                key = "cieloTapInformation",
                                value = "Máquina não conectada no dispositivo e dispositivo sem NFC"
                            )
                            resultListener.onRetryConnectCardReader(presenter.onGetMyTerminalInfo())
                        }

                        else -> {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: conexão card reader",
                                key = "cieloTapInformation",
                                value = "Falha na conexão com o card reader errorcode: $p1"
                            )
                            showErrorSDK()
                        }
                    }
                }

                override fun onUpdateTables() {
                    datadogEvent.LoggerInfo(
                        message = "Cielo TAP: conexão card reader",
                        key = "cieloTapInformation",
                        value = "Atualizando tabelas"
                    )
                    onShowLoading(R.string.tap_on_phone_update_tables_card_reader)
                }

            })
    }

    override fun onMakeTransaction(transactionInfo: TapOnPhoneSaleInfoModel) {
        with(transactionInfo) {
            tapInstance?.apply {
                setAidType(aidType)
                makeTransaction(
                    transactionValue,
                    currency,
                    transactionType,
                    extendedTransactionData,
                    additionalData,
                    object : PaymentListener {
                        override fun onPaymentResult(
                            p0: PaymentStatus?,
                            p1: PaymentData?,
                            p2: Short
                        ) {
                            when {
                                p0 == PaymentStatus.PAYMENT_APPROVED -> {
                                    transactionApprovedAction(getReceipt(p1))
                                }

                                p2 == (CARD_READER_NOT_CONNECT).toShort() -> {
                                    resultListener.onConnectCardReaderError()
                                }

                                p2 == (ERROR_CODE_FORTY_TWO_IN_MAKE_TRANSACTION).toShort() -> {
                                    resultListener.onMakeTransactionErrorWithCodeFortyTwo()
                                }

                                else -> {
                                    resultListener.onTransactionFailedError(p2)
                                }
                            }
                            val resultMessage =
                                if (p0 != PaymentStatus.PAYMENT_APPROVED) "Falha na captura do pagamento, errorcode $p2"
                                else "Sucesso na captura do pagamento"

                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: venda",
                                key = "cieloTapInformation",
                                value = resultMessage
                            )
                        }

                        override fun onPaymentCancel() {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: venda",
                                key = "cieloTapInformation",
                                value = "Pagamento cancelado"
                            )
                            transactionCancelled()
                        }

                        override fun onTimeExpired() {
                            datadogEvent.LoggerInfo(
                                message = "Cielo TAP: venda",
                                key = "cieloTapInformation",
                                value = "Timeout no pagamento"
                            )
                            transactionTimeExpired()
                        }
                    },
                    arrayListOf<ReaderType>(ReaderType.TAP_ON_PHONE, ReaderType.MPOS_D140)
                )

            } ?: showError()

        }
    }

    private fun getReceipt(
        transactionData: PaymentData?,
    ): TransactionReceiptData {
        val dynamicBankData = transactionData?.dynamicBankData?.let {
            Gson().fromJson(it, DynamicBankData::class.java)
        }
        val receiptInfo = dynamicBankData?.receiptInfo?.let {
            Gson().fromJson(it, ReceiptInfo::class.java)
        }

        val clearDate = receiptInfo?.paymentDate
        val date = clearDate?.formatterDate(
            mask = FORMAT_TIME_CIELO_TAP,
            resultMask = SIMPLE_DT_FORMAT_MASK
        )
        val hour = clearDate?.isoDateToBrHourAndMinute(
            mask = FORMAT_TIME_CIELO_TAP,
            newFormat = SIMPLE_HOUR_MINUTE_24h
        )

        return TransactionReceiptData(
            doc = transactionData?.rrn,
            date = date,
            hour = hour,
            cardNumber = transactionData?.cardLastDigits,
            brand = transactionData?.brand?.name,
            value = transactionData?.amount?.toString(),
            applicationId = transactionData?.applicationId,
            installments = receiptInfo?.installments,
            transactionType = transactionData?.transactionType?.name,
            receiptInfo = receiptInfo
        )
    }
}
