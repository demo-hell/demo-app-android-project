package br.com.mobicare.cielo.pixMVVM.presentation.account.utils

sealed class PixProfileUiState {
    object Success : PixProfileUiState()
    object Error : PixProfileUiState()
}
