package br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.orZero
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundReceiptsUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundReceiptsSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.models.PixRequestRefundStore
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PixRequestRefundViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixRefundReceiptsUseCase: GetPixRefundReceiptsUseCase,
    private val getPixAccountBalanceUseCase: GetPixAccountBalanceUseCase,
    private val refundReceiptsSuccessStateHandler: PixRefundReceiptsSuccessStateHandler
) : ViewModel() {

    private var _refundReceiptsUiState = MutableLiveData<PixRefundReceiptsUiState>()
    val refundReceiptsUiState: LiveData<PixRefundReceiptsUiState> get() = _refundReceiptsUiState

    private var _transferDetail: PixTransferDetail? = null
    val transferDetail get() = _transferDetail

    private var _refundReceipts: PixRefundReceipts? = null
    val refundReceipts get() = _refundReceipts

    private var _currentBalance: Double? = null
    val currentBalance get() = _currentBalance

    private var _store = PixRequestRefundStore()
    val store get() = _store

    val availableAmountToRefund get() = refundReceipts?.totalAmountPossibleReversal

    fun setAmount(amount: Double) {
        _store = _store.copy(amount = amount)
    }

    fun setMessage(message: String) {
        _store = _store.copy(message = message.ifBlank { null })
    }

    fun validate() = _store.validateAmount(availableAmountToRefund.orZero(), currentBalance)

    fun getRefundReceipts(transferDetail: PixTransferDetail? = null) {
        transferDetail?.let { _transferDetail = it }

        viewModelScope.launch {
            setRefundReceiptsState(PixRefundReceiptsUiState.Loading)

            val getBalanceAsync = if (currentBalance == null) {
                async { getPixAccountBalanceUseCase() }
            } else null

            val getRefundReceiptsAsync = async {
                getPixRefundReceiptsUseCase(
                    GetPixRefundReceiptsUseCase.Params(transferDetail?.idEndToEnd)
                )
            }

            getBalanceAsync?.await()?.onSuccess {
                _currentBalance = it.currentBalance
            }

            getRefundReceiptsAsync.await().onSuccess {
                _refundReceipts = it
                setRefundReceiptsSuccessState(it)
            }.onEmpty {
                setRefundReceiptsErrorState()
            }.onError {
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = it.apiException.newErrorMessage,
                    onErrorAction = ::setRefundReceiptsErrorState
                )
            }
        }
    }

    private fun setRefundReceiptsSuccessState(refundReceipts: PixRefundReceipts) {
        setRefundReceiptsState(
            refundReceiptsSuccessStateHandler(refundReceipts, transferDetail?.expiredReversal)
        )
    }

    private fun setRefundReceiptsErrorState() {
        setRefundReceiptsState(
            if (transferDetail?.expiredReversal == true) {
                PixRefundReceiptsUiState.ErrorWithExpiredRefund
            } else {
                PixRefundReceiptsUiState.ErrorWithNotExpiredRefund
            }
        )
    }

    private fun setRefundReceiptsState(state: PixRefundReceiptsUiState) {
        _refundReceiptsUiState.postValue(state)
    }

}