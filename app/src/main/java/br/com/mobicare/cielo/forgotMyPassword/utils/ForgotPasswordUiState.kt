package br.com.mobicare.cielo.forgotMyPassword.utils

sealed class ForgotPasswordUiState<out T> {
    open class Success<T>(val data: T) : ForgotPasswordUiState<T>()
    class Error(val message: String? = null) : ForgotPasswordUiState<Nothing>()
    object ErrorAkamai : ForgotPasswordUiState<Nothing>()
    object Loading : ForgotPasswordUiState<Nothing>()
}