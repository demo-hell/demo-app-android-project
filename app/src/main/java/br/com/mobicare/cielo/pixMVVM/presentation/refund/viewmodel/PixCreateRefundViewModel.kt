package br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixRefundUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailFullUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundDetailSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.models.PixCreateRefundStore
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixCreateRefundUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState
import kotlinx.coroutines.launch

class PixCreateRefundViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val createPixRefundUseCase: CreatePixRefundUseCase,
    private val getPixRefundDetailFullUseCase: GetPixRefundDetailFullUseCase,
    private val refundDetailSuccessStateHandler: PixRefundDetailSuccessStateHandler
) : ViewModel() {

    private var _createRefundUiState = MutableLiveData<PixCreateRefundUiState>()
    val createRefundUiState: LiveData<PixCreateRefundUiState> get() = _createRefundUiState

    private var _refundDetailUiState = MutableLiveData<PixRefundDetailUiState>()
    val refundDetailUiState: LiveData<PixRefundDetailUiState> get() = _refundDetailUiState

    private var _refundDetailFull: PixRefundDetailFull? = null
    val refundDetailFull get() = _refundDetailFull

    private var _transactionCode: String? = null
    val transactionCode get() = _transactionCode

    fun createRefund(store: PixCreateRefundStore) {
        viewModelScope.launch {
            createPixRefundUseCase(
                CreatePixRefundUseCase.Params(
                    token = store.otpCode,
                    request = PixRefundCreateRequest(
                        idEndToEndOriginal = store.idEndToEnd,
                        idTx = store.idTx,
                        reversalReason = store.message,
                        amount = store.amount,
                        fingerprint = store.fingerprint,
                    )
                )
            ).onSuccess {
                _transactionCode = it.transactionCode
                setCreateRefundState(PixCreateRefundUiState.Success)
            }.onEmpty {
                setCreateRefundState(PixCreateRefundUiState.GenericError())
            }.onError {
                handleError(it.apiException.newErrorMessage) { error ->
                    setCreateRefundErrorState(error)
                }
            }
        }
    }

    fun getRefundDetail() {
        viewModelScope.launch {
            setRefundDetailState(PixRefundDetailUiState.Loading)

            getPixRefundDetailFullUseCase(
                GetPixRefundDetailFullUseCase.Params(transactionCode)
            ).onSuccess {
                _refundDetailFull = it
                setRefundDetailState(refundDetailSuccessStateHandler(it.refundDetail))
            }.onEmpty {
                setRefundDetailState(PixRefundDetailUiState.Error)
            }.onError {
                setRefundDetailState(PixRefundDetailUiState.Error)
            }
        }
    }

    private suspend fun handleError(error: NewErrorMessage, onErrorAction: (NewErrorMessage) -> Unit) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = { onErrorAction(error) }
        )
    }

    private fun setCreateRefundErrorState(error: NewErrorMessage) {
        when {
            error.flagErrorCode.contains(Text.OTP) ->
                setCreateRefundState(PixCreateRefundUiState.TokenError(error))
            error.httpCode == HTTP_ENHANCE ->
                setCreateRefundState(PixCreateRefundUiState.Unprocessable(error))
            else ->
                setCreateRefundState(PixCreateRefundUiState.GenericError(error))
        }
    }

    private fun setCreateRefundState(state: PixCreateRefundUiState) {
        _createRefundUiState.postValue(state)
    }

    private fun setRefundDetailState(state: PixRefundDetailUiState) {
        _refundDetailUiState.postValue(state)
    }

}