package br.com.mobicare.cielo.pixMVVM.presentation.refund.utils

sealed class PixRefundDetailUiState {
    object Loading : PixRefundDetailUiState()

    abstract class Success : PixRefundDetailUiState()
    object StatusExecuted : Success()
    object StatusPending : Success()
    object StatusNotExecuted : Success()

    object Error : PixRefundDetailUiState()
}
