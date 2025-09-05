package br.com.mobicare.cielo.transparentLogin.utils

sealed class TransparentLoginUiState {
    data class Error(val errorMessage: String? = null) : TransparentLoginUiState()
    object Success : TransparentLoginUiState()
    object ManualLogin: TransparentLoginUiState()
    object TransparentLogin: TransparentLoginUiState()
}