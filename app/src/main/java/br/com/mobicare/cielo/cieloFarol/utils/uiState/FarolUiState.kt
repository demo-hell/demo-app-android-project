package br.com.mobicare.cielo.cieloFarol.utils.uiState

sealed class FarolUiState<out T> {
    open class Success<T>(val data: T) : FarolUiState<T>()
    class Error(val message: String? = null): FarolUiState<Nothing>()
    object Empty : FarolUiState<Nothing>()
}