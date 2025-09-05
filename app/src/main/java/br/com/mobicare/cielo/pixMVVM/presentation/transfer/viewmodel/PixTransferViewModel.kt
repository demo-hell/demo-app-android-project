package br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.dateUtils.plusMonths
import br.com.cielo.libflue.util.dateUtils.plusWeeks
import br.com.cielo.libflue.util.dateUtils.removeTimeAttributes
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.constants.TWO_LONG
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.getNumberOfMonthsBetweenDates
import br.com.mobicare.cielo.commons.utils.getNumberOfWeeksBetweenDates
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.commons.utils.isToday
import br.com.mobicare.cielo.commons.utils.toLocalDate
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferToBankAccountUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferWithKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.models.PixBankAccountStore
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums.PixPeriodRecurrence
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixBankAccountData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixKeyData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixRecurrenceData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixTransferStore
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixValidateKeyData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferReceiptUiState
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants.PIX_FREQUENCY_TIME_ONE
import kotlinx.coroutines.launch
import java.util.Calendar

class PixTransferViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val requestPixTransferWithKeyUseCase: RequestPixTransferWithKeyUseCase,
    private val requestPixTransferToBankAccountUseCase: RequestPixTransferToBankAccountUseCase,
    private val getPixTransferDetailsUseCase: GetPixTransferDetailsUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<PixTransferUiState>()
    val uiState get(): LiveData<PixTransferUiState> = _uiState

    private val _receiptState = MutableLiveData<PixTransferReceiptUiState>()
    val receiptState get(): LiveData<PixTransferReceiptUiState> = _receiptState

    private val _ftRecurrenceEnabled = MutableLiveData<Boolean>()
    val ftRecurrenceEnabled get(): LiveData<Boolean> = _ftRecurrenceEnabled

    private var _pixRecurrenceIsSelected = false
    val pixRecurrenceIsSelected get() = _pixRecurrenceIsSelected

    private var _store = PixTransferStore()
    val store get() = _store

    var keyData: PixKeyData<*>? = null

    private var transferResult: PixTransferResult? = null

    private val isScheduledTransfer get() = store.schedulingDate != null

    private var fingerprint = EMPTY_STRING

    fun setFingerPrint(value: String) {
        fingerprint = value
    }

    fun setAmount(amount: Double) {
        _store = _store.copy(amount = amount)
    }

    fun setMessage(message: String) {
        _store = _store.copy(message = message.ifBlank { null })
    }

    fun setSchedulingDate(date: Calendar?) {
        _store =
            _store.copy(
                schedulingDate = date?.takeIf { it.isToday().not() },
                recurrenceData =
                    _store.recurrenceData.copy(
                        startDate = date ?: Calendar.getInstance().removeTimeAttributes(),
                        endDate = null,
                    ),
            )
    }

    fun saveRecurrenceData(recurrenceData: PixRecurrenceData) {
        _store =
            _store.copy(
                schedulingDate = recurrenceData.startDate.takeIf { it.isToday().not() },
                recurrenceData = recurrenceData,
            )
    }

    fun selectPixRecurrence(isSelected: Boolean) {
        _pixRecurrenceIsSelected = isSelected
        if (isSelected.not()) {
            _store =
                _store.copy(
                    recurrenceData =
                        PixRecurrenceData(
                            startDate = _store.schedulingDate ?: Calendar.getInstance().removeTimeAttributes(),
                        ),
                )
        }
    }

    fun getFeatureToggleRecurrence() {
        viewModelScope.launch {
            getFeatureTogglePreference(FeatureTogglePreference.PIX_SHOW_BUTTON_TRANSFER_RECURRENCE)
                .onSuccess {
                    _ftRecurrenceEnabled.value = it
                }
        }
    }

    fun requestTransfer(token: String) {
        when (keyData) {
            is PixValidateKeyData -> requestTransferWithKey(token, keyData?.data as? PixValidateKey)
            is PixBankAccountData -> requestTransferToBankAccount(token, keyData?.data as? PixBankAccountStore)
            else -> setState(PixTransferUiState.GenericError())
        }
    }

    private fun requestTransferWithKey(
        token: String,
        validateKey: PixValidateKey?,
    ) {
        viewModelScope.launch {
            requestPixTransferWithKeyUseCase(
                RequestPixTransferWithKeyUseCase.Params(
                    otpCode = token,
                    request =
                        PixTransferKeyRequest(
                            finalAmount = store.amount,
                            endToEndId = validateKey?.endToEndId,
                            message = store.message,
                            schedulingDate = schedulingDateRequest(),
                            payee =
                                PixTransferKeyRequest.Payee(
                                    key = validateKey?.key,
                                    keyType = validateKey?.keyType,
                                ),
                            frequencyTime = frequencyTimeRequest(),
                            schedulingFinalDate = schedulingFinalDateRequest(),
                            fingerprint = fingerprint
                        ),
                ),
            ).onSuccess {
                transferResult = it
                setSuccessState()
            }.onEmpty {
                setState(PixTransferUiState.GenericError())
            }.onError {
                handleError(it.apiException.newErrorMessage) { error ->
                    setErrorState(error)
                }
            }
        }
    }

    private fun requestTransferToBankAccount(
        token: String,
        bankAccountStore: PixBankAccountStore?,
    ) {
        viewModelScope.launch {
            requestPixTransferToBankAccountUseCase(
                RequestPixTransferToBankAccountUseCase.Params(
                    otpCode = token,
                    request =
                        PixTransferBankAccountRequest(
                            finalAmount = store.amount,
                            message = store.message,
                            payee =
                                bankAccountStore?.run {
                                    PixTransferBankAccountRequest.Payee(
                                        bankAccountNumber = bankAccountNumber,
                                        bankAccountType = bankAccountType?.key,
                                        bankBranchNumber = bankBranchNumber,
                                        beneficiaryType = beneficiaryType?.key,
                                        documentNumber = documentNumber,
                                        ispb = bank?.ispb?.toIntOrNull(),
                                        name = recipientName,
                                    )
                                },
                            schedulingDate = schedulingDateRequest(),
                            frequencyTime = frequencyTimeRequest(),
                            schedulingFinalDate = schedulingFinalDateRequest(),
                            fingerprint = fingerprint
                        ),
                ),
            ).onSuccess {
                transferResult = it
                setSuccessState()
            }.onEmpty {
                setState(PixTransferUiState.GenericError())
            }.onError {
                handleError(it.apiException.newErrorMessage) { error ->
                    setErrorState(error)
                }
            }
        }
    }

    fun getTransferDetails() {
        viewModelScope.launch {
            setReceiptState(PixTransferReceiptUiState.Loading)

            getPixTransferDetailsUseCase(
                GetPixTransferDetailsUseCase.Params(
                    transferResult?.endToEndId,
                    transferResult?.transactionCode,
                ),
            ).onSuccess {
                setReceiptState(PixTransferReceiptUiState.Success(it))
            }.onEmpty {
                setReceiptState(PixTransferReceiptUiState.Error())
            }.onError {
                handleError(it.apiException.newErrorMessage) { error ->
                    setReceiptState(PixTransferReceiptUiState.Error(error))
                }
            }
        }
    }

    private fun setSuccessState() {
        if (isScheduledTransfer) {
            setState(PixTransferUiState.TransferScheduled)
        } else {
            setState(PixTransferUiState.TransferSent)
        }
    }

    private suspend fun handleError(
        error: NewErrorMessage,
        onErrorAction: (NewErrorMessage) -> Unit,
    ) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = { onErrorAction(error) },
            onHideLoading = { onErrorAction(error) },
        )
    }

    private fun setErrorState(error: NewErrorMessage) {
        if (error.flagErrorCode.contains(Text.OTP)) {
            setState(PixTransferUiState.TokenError(error))
        } else if (error.flagErrorCode == ERROR_CODE_TOO_MANY_REQUESTS) {
            setState(PixTransferUiState.TooManyRequestsError(error))
        } else {
            setState(PixTransferUiState.GenericError(error))
        }
    }

    private fun setState(state: PixTransferUiState) {
        _uiState.value = state
        _uiState.value = PixTransferUiState.DoNothing
    }

    private fun setReceiptState(state: PixTransferReceiptUiState) {
        _receiptState.value = state
    }

    private fun schedulingDateRequest(): String? =
        if (pixRecurrenceIsSelected) {
            store.recurrenceData.startDate.toString(SIMPLE_DATE_INTERNATIONAL)
        } else {
            store.schedulingDate?.toString(SIMPLE_DATE_INTERNATIONAL)
        }

    private fun frequencyTimeRequest(): String? =
        when {
            pixRecurrenceIsSelected -> store.recurrenceData.period?.name
            isScheduledTransfer -> PIX_FREQUENCY_TIME_ONE
            else -> null
        }

    private fun schedulingFinalDateRequest(): String? =
        if (pixRecurrenceIsSelected) {
            getEndDateFromRecurrence()?.toString(SIMPLE_DATE_INTERNATIONAL)
        } else {
            null
        }

    private fun getEndDateFromRecurrence(): Calendar? {
        return store.recurrenceData.endDate.ifNull {
            val multiplier = getMultiplier()
            val startDate = store.recurrenceData.startDate

            return when (store.recurrenceData.period) {
                PixPeriodRecurrence.WEEKLY -> multiplier?.let { startDate.plusWeeks(it) }
//                PixPeriodRecurrence.BIWEEKLY -> multiplier?.let { startDate.plusWeeks(it * TWO) }
                else -> multiplier?.let { startDate.plusMonths(it) }
            }
        }
    }

    private fun getMultiplier(): Int? {
        val startDate = store.recurrenceData.startDate.toLocalDate()
        val endDate = startDate.plusYears(TWO_LONG)

        return when (store.recurrenceData.period) {
            PixPeriodRecurrence.WEEKLY -> getNumberOfWeeksBetweenDates(startDate, endDate)
//                PixPeriodRecurrence.BIWEEKLY -> getNumberOfWeeksBetweenDates(startDate, endDate)?.div(TWO)
            else -> getNumberOfMonthsBetweenDates(startDate, endDate)
        }?.toInt()
    }
}
