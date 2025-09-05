package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferScheduledBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferBalanceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PixScheduledTransferBalanceViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val requestPixTransferScheduledBalanceUseCase: RequestPixTransferScheduledBalanceUseCase
) : ViewModel() {

    private val _scheduledBalanceState =
        MutableStateFlow<PixScheduledTransferBalanceUiState>(PixScheduledTransferBalanceUiState.Idle)
    val scheduledBalanceState get() = _scheduledBalanceState.asStateFlow()

    fun requestTransfer(token: String) {
        viewModelScope.launch {
            requestPixTransferScheduledBalanceUseCase(
                RequestPixTransferScheduledBalanceUseCase.Params(token)
            ).onSuccess {
                setScheduledBalanceState(PixScheduledTransferBalanceUiState.Success)
            }.onError {
                handleError(it.apiException.newErrorMessage) { error ->
                    handleErrorState(error)
                }
            }.onEmpty {
                setScheduledBalanceState(PixScheduledTransferBalanceUiState.GenericError)
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

    private fun handleErrorState(error: NewErrorMessage) {
        setScheduledBalanceState(
            if (error.flagErrorCode == INSUFFICIENT_BALANCE) {
                PixScheduledTransferBalanceUiState.InsufficientBalanceError
            } else {
                PixScheduledTransferBalanceUiState.GenericError
            }
        )
    }

    private fun setScheduledBalanceState(state: PixScheduledTransferBalanceUiState) {
        _scheduledBalanceState.value = state
    }

    companion object {
        const val INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE"
    }

}