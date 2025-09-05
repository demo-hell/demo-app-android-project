package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.dateUtils.removeTimeAttributes
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_400
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_500
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.getTotalValueChange
import br.com.mobicare.cielo.commons.utils.isToday
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferWithKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixPaymentQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import kotlinx.coroutines.launch
import java.util.Calendar

class PixQRCodePaymentViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val requestPixTransferWithKeyUseCase: RequestPixTransferWithKeyUseCase,
) : ViewModel() {
    private var fingerprint: String? = null

    private val _uiState = MutableLiveData<PixPaymentQRCodeUIState>()
    val uiState: LiveData<PixPaymentQRCodeUIState> get() = _uiState

    private val _pixDecodeQRCode = MutableLiveData<PixDecodeQRCode>()
    val pixDecodeQRCode: LiveData<PixDecodeQRCode> get() = _pixDecodeQRCode

    private val _paymentAmount = MutableLiveData<Double>()
    val paymentAmount: LiveData<Double> get() = _paymentAmount

    private val _changeAmount = MutableLiveData<Double>()
    val changeAmount: LiveData<Double> get() = _changeAmount

    private val _finalAmount = MutableLiveData<Double>()
    val finalAmount: LiveData<Double> get() = _finalAmount

    private val _paymentDate = MutableLiveData(Calendar.getInstance().removeTimeAttributes())
    val paymentDate: LiveData<Calendar> get() = _paymentDate

    private val _optionalMessage = MutableLiveData(EMPTY_STRING)
    val optionalMessage: LiveData<String> get() = _optionalMessage

    fun setPixDecodeQRCode(pixDecodeQRCode: PixDecodeQRCode) {
        _pixDecodeQRCode.value = pixDecodeQRCode
        setPaymentAmount(PixQRCodeUtils.getPaymentAmount(pixDecodeQRCode))
        pixDecodeQRCode.changeAmount?.let {
            if (PixQRCodeUtils.isPixTypeChange(pixDecodeQRCode)) {
                setChangeAmount(it)
            }
        }
    }

    fun setPaymentAmount(paymentAmount: Double) {
        _paymentAmount.value = paymentAmount
        calculateFinalAmount()
    }

    fun setChangeAmount(changeAmount: Double) {
        _changeAmount.value = changeAmount
        calculateFinalAmount()
    }

    fun setPaymentDate(paymentDate: Calendar) {
        _paymentDate.value = paymentDate
    }

    fun setOptionalMessage(optionalMessage: String) {
        _optionalMessage.value = optionalMessage
    }

    fun setFingerprint(fingerprint: String) {
        this.fingerprint = fingerprint
    }

    fun toPay(otpCode: String) {
        viewModelScope.launch {
            requestPixTransferWithKeyUseCase
                .invoke(
                    RequestPixTransferWithKeyUseCase.Params(
                        otpCode,
                        request = generatePixTransferKeyRequest(),
                    ),
                ).onSuccess {
                    processSuccess(it)
                }.onEmpty {
                    setUIState(PixPaymentQRCodeUIState.GenericError(null))
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                }
        }
    }

    private fun calculateFinalAmount() {
        _finalAmount.value = pixDecodeQRCode.value?.let {
            if (PixQRCodeUtils.isPixTypeChange(it)) {
                getTotalValueChange(changeAmount.value, it.originalAmount)
            } else {
                paymentAmount.value
            }
        } ?: ZERO_DOUBLE
    }

    private fun generatePixTransferKeyRequest(): PixTransferKeyRequest? =
        pixDecodeQRCode.value?.let {
            PixTransferKeyRequest(
                fingerprint = fingerprint,
                endToEndId = it.endToEndId,
                message = optionalMessage.value,
                payee =
                    PixTransferKeyRequest.Payee(
                        key = it.key,
                        keyType = it.keyType,
                    ),
                pixType = it.pixType.name,
                transferType =
                    pixDecodeQRCode.value
                        ?.type
                        ?.transferType
                        ?.name
                        .orEmpty(),
                idTx = it.idTx,
                changeAmount = changeAmount.value,
                purchaseAmount = if (changeAmount.value != null) paymentAmount.value else null,
                agentMode = PixQRCodeUtils.getAgentMode(it),
                agentWithdrawalIspb = PixQRCodeUtils.getAgentWithdrawalIspb(it),
                finalAmount = finalAmount.value,
                schedulingDate = getSchedulingDate(),
                frequencyTime = getFrequencyTime(),
            )
        }

    private fun getSchedulingDate(): String? = paymentDate.value?.takeIf { it.isToday().not() }?.toString(SIMPLE_DATE_INTERNATIONAL)

    private fun getFrequencyTime(): String? =
        if (paymentDate.value?.isToday()?.not() == true) {
            PixConstants.PIX_FREQUENCY_TIME_ONE
        } else {
            null
        }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = {
                processError(error)
            },
            onHideLoading = {
                setUIState(PixPaymentQRCodeUIState.HideLoading)
            },
        )
    }

    private fun processSuccess(transferResult: PixTransferResult) {
        setUIState(
            when (transferResult.transactionStatus) {
                PixTransactionStatus.EXECUTED -> {
                    PixPaymentQRCodeUIState.TransactionExecuted(
                        transferResult,
                        typeQRCode = pixDecodeQRCode.value?.pixType ?: PixQrCodeOperationType.TRANSFER,
                    )
                }

                PixTransactionStatus.SCHEDULED, PixTransactionStatus.SCHEDULED_EXECUTED -> {
                    PixPaymentQRCodeUIState.TransactionScheduled(transferResult)
                }

                PixTransactionStatus.PENDING, PixTransactionStatus.PROCESSING -> {
                    PixPaymentQRCodeUIState.TransactionProcessing
                }

                else -> PixPaymentQRCodeUIState.TransactionFailed
            },
        )
    }

    private fun processError(error: NewErrorMessage) {
        setUIState(
            when {
                error.flagErrorCode.contains(Text.OTP) -> PixPaymentQRCodeUIState.TokenError(error)
                error.httpCode in HTTP_STATUS_400 until HTTP_STATUS_500 -> PixPaymentQRCodeUIState.FourHundredError(error)
                else -> PixPaymentQRCodeUIState.GenericError(error)
            },
        )
    }

    private fun setUIState(state: PixPaymentQRCodeUIState) {
        _uiState.value = state
        _uiState.value = PixPaymentQRCodeUIState.DoNothing
    }
}
