package br.com.mobicare.cielo.pixMVVM.presentation.account.utils

sealed class PixScheduledTransferBalanceUiState {
    object Idle : PixScheduledTransferBalanceUiState()
    object Success : PixScheduledTransferBalanceUiState()

    abstract class Error : PixScheduledTransferBalanceUiState()
    object GenericError : Error()
    object InsufficientBalanceError : Error()
}
