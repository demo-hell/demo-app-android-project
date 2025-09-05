package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CancelPixTransferScheduleUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailFullUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundReceiptsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferScheduleDetailUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundReceiptsResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixScheduleResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixTransferResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundReceiptsUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailUiState
import kotlinx.coroutines.launch

class PixNewExtractDetailViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixRefundReceiptsUseCase: GetPixRefundReceiptsUseCase,
    private val getPixRefundDetailUseCase: GetPixRefundDetailFullUseCase,
    private val getPixTransferDetailsUseCase: GetPixTransferDetailsUseCase,
    private val getPixTransferScheduleDetailUseCase: GetPixTransferScheduleDetailUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
    private val cancelPixTransferScheduleUseCase: CancelPixTransferScheduleUseCase,
    private val transferResultHandler: PixTransferResultHandler,
    private val refundResultHandler: PixRefundResultHandler,
    private val scheduleResultHandler: PixScheduleResultHandler,
    private val refundReceiptsResultHandler: PixRefundReceiptsResultHandler,
) : ViewModel() {
    private val _transferState = MutableLiveData<PixExtractDetailUiState<PixTransferUiResult>>()
    val transferState: LiveData<PixExtractDetailUiState<PixTransferUiResult>> = _transferState

    private val _refundState = MutableLiveData<PixExtractDetailUiState<PixRefundUiResult>>()
    val refundState: LiveData<PixExtractDetailUiState<PixRefundUiResult>> = _refundState

    private val _scheduleState = MutableLiveData<PixExtractDetailUiState<PixScheduleUiResult>>()
    val scheduleState: LiveData<PixExtractDetailUiState<PixScheduleUiResult>> = _scheduleState

    private val _cancelScheduleState = MutableLiveData<PixExtractDetailCancelScheduleUIState>()
    val cancelScheduleState: LiveData<PixExtractDetailCancelScheduleUIState> = _cancelScheduleState

    private val _refundReceiptsState =
        MutableLiveData<PixExtractDetailUiState<PixRefundReceiptsUiResult>>()
    val refundReceiptsState: LiveData<PixExtractDetailUiState<PixRefundReceiptsUiResult>> =
        _refundReceiptsState

    private var transactionCode: String? = null
    private var schedulingCode: String? = null
    private var _endToEndId: String? = null
    private var isRefund = false

    val endToEndId get() = _endToEndId.orEmpty()

    private var _transferDetail = PixTransferDetail()
    val transferDetail get() = _transferDetail

    private var _refundDetailFull = PixRefundDetailFull()
    val refundDetailFull get() = _refundDetailFull

    private var schedulingDetail: PixSchedulingDetail? = null

    private var ftShowButtonTransactionAnalyze = false

    private val isScheduling get() = schedulingCode != null

    private val isExecutedTransferCredit
        get() =
            transferDetail.let {
                it.transactionType == PixTransactionType.TRANSFER_CREDIT &&
                    it.transactionStatus == PixTransactionStatus.EXECUTED
            }

    val isRecurrentTransferSchedule get() = schedulingDetail?.type == PixType.SCHEDULE_RECURRENCE_DEBIT

    private val enable
        get() = schedulingDetail?.enable ?: _transferDetail.enable ?: _refundDetailFull.enable

    val isShowButtonRefund get() = enable?.refund == true
    val isShowButtonCancelSchedule get() = enable?.cancelSchedule == true
    val isShowButtonRequestAnalysis get() = enable?.requestAnalysis == true && ftShowButtonTransactionAnalyze
    val isShowButtonAccessOriginalTransaction get() = refundDetailFull.refundDetail?.idEndToEndOriginal != null

    fun start(
        transactionCode: String?,
        endToEndId: String?,
        schedulingCode: String?,
        isRefund: Boolean?,
    ) {
        this.transactionCode = transactionCode
        this.schedulingCode = schedulingCode
        this.isRefund = isRefund ?: false
        _endToEndId = endToEndId

        getFeatureToggle()
    }

    fun cancelTransferSchedule(otpCode: String) {
        viewModelScope.launch {
            cancelPixTransferScheduleUseCase(
                CancelPixTransferScheduleUseCase.Params(
                    otpCode,
                    PixScheduleCancelRequest(schedulingCode.orEmpty()),
                ),
            ).onSuccess {
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.CancelScheduleSuccess,
                )
            }.onEmpty {
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.CancelScheduleError(),
                )
            }.onError {
                handleError(it.apiException.newErrorMessage)
            }
        }
    }

    fun getScheduleDetailsAfterCancelTransferSchedule() {
        viewModelScope.launch {
            getScheduleDetails(schedulingCode.orEmpty(), isCancelSchedule = true)
        }
    }

    private fun getFeatureToggle() {
        viewModelScope.launch {
            getFeatureTogglePreferenceUseCase.invoke(FeatureTogglePreference.PIX_BUTTON_TRANSACTION_ANALYZE)
                .onSuccess { result ->
                    ftShowButtonTransactionAnalyze = result
                    getTransactionDetails()
                }
        }
    }

    private suspend fun getTransactionDetails() {
        if (isRefund) {
            getRefundDetails(transactionCode.orEmpty())
        } else if (isScheduling) {
            getScheduleDetails(schedulingCode, isCancelSchedule = false)
        } else {
            getTransferDetails()
        }
    }

    private suspend fun getTransferDetails() {
        viewModelScope.launch {
            setTransferState(PixExtractDetailUiState.Loading)

            getPixTransferDetailsUseCase(
                GetPixTransferDetailsUseCase.Params(endToEndId, transactionCode),
            ).onSuccess {
                _transferDetail = it

                setTransferState(
                    PixExtractDetailUiState.Success(transferResultHandler(it)),
                )

                if (isExecutedTransferCredit) {
                    getRefundReceipts()
                }
            }.onEmpty {
                setTransferState(PixExtractDetailUiState.Error())
            }.onError {
                setTransferState(PixExtractDetailUiState.Error(it.apiException.newErrorMessage))
            }
        }
    }

    private suspend fun getRefundReceipts() {
        setRefundReceiptsState(PixExtractDetailUiState.Loading)

        getPixRefundReceiptsUseCase(
            GetPixRefundReceiptsUseCase.Params(endToEndId),
        ).onSuccess {
            setRefundReceiptsState(
                PixExtractDetailUiState.Success(
                    refundReceiptsResultHandler(it, transferDetail),
                ),
            )
        }.onEmpty {
            setRefundReceiptsState(PixExtractDetailUiState.Error())
        }.onError {
            setRefundReceiptsState(PixExtractDetailUiState.Error(it.apiException.newErrorMessage))
        }
    }

    private suspend fun getRefundDetails(transactionCode: String) {
        setRefundState(PixExtractDetailUiState.Loading)

        getPixRefundDetailUseCase(
            GetPixRefundDetailFullUseCase.Params(transactionCode),
        ).onSuccess { refundFull ->
            _refundDetailFull = refundFull

            refundFull.refundDetail?.let { refund ->
                setRefundState(PixExtractDetailUiState.Success(refundResultHandler(refund)))
            }.ifNull {
                setRefundState(PixExtractDetailUiState.Error())
            }
        }.onEmpty {
            setRefundState(PixExtractDetailUiState.Error())
        }.onError {
            setRefundState(PixExtractDetailUiState.Error(it.apiException.newErrorMessage))
        }
    }

    private suspend fun getScheduleDetails(
        schedulingCode: String?,
        isCancelSchedule: Boolean,
    ) {
        if (isCancelSchedule) {
            setCancelScheduleState(PixExtractDetailCancelScheduleUIState.ShowLoading)
        } else {
            setScheduleState(PixExtractDetailUiState.Loading)
        }

        getPixTransferScheduleDetailUseCase(
            GetPixTransferScheduleDetailUseCase.Params(schedulingCode),
        ).onSuccess {
            schedulingDetail = it
            if (isCancelSchedule) {
                setCancelScheduleState(PixExtractDetailCancelScheduleUIState.HideLoading)
                setCancelScheduleState(
                    if (it.status == PixTransactionStatus.CANCELLED) {
                        PixExtractDetailCancelScheduleUIState.ScheduleDetailSuccess
                    } else {
                        PixExtractDetailCancelScheduleUIState.ScheduleDetailPending
                    },
                )
            } else {
                setScheduleState(PixExtractDetailUiState.Success(scheduleResultHandler(it)))
            }
        }.onEmpty {
            if (isCancelSchedule) {
                setCancelScheduleState(PixExtractDetailCancelScheduleUIState.HideLoading)
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.ScheduleDetailError(),
                )
            } else {
                setScheduleState(PixExtractDetailUiState.Error())
            }
        }.onError {
            val errorMessage = it.apiException.newErrorMessage
            if (isCancelSchedule) {
                setCancelScheduleState(PixExtractDetailCancelScheduleUIState.HideLoading)
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.ScheduleDetailError(errorMessage),
                )
            } else {
                setScheduleState(PixExtractDetailUiState.Error(errorMessage))
            }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.HideLoadingCancelSchedule,
                )
            },
            onErrorAction = {
                setCancelScheduleState(
                    PixExtractDetailCancelScheduleUIState.CancelScheduleError(error),
                )
            },
        )
    }

    private fun setTransferState(state: PixExtractDetailUiState<PixTransferUiResult>) {
        _transferState.postValue(state)
    }

    private fun setRefundState(state: PixExtractDetailUiState<PixRefundUiResult>) {
        _refundState.postValue(state)
    }

    private fun setRefundReceiptsState(state: PixExtractDetailUiState<PixRefundReceiptsUiResult>) {
        _refundReceiptsState.postValue(state)
    }

    private fun setScheduleState(state: PixExtractDetailUiState<PixScheduleUiResult>) {
        _scheduleState.postValue(state)
    }

    private fun setCancelScheduleState(state: PixExtractDetailCancelScheduleUIState) {
        _cancelScheduleState.value = state
    }
}
