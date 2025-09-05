package br.com.mobicare.cielo.pixMVVM.presentation.account.utils

sealed class PixReceiptMethodUiState {
    object Loading : PixReceiptMethodUiState()
    object Success : PixReceiptMethodUiState()
    object Error : PixReceiptMethodUiState()
}
