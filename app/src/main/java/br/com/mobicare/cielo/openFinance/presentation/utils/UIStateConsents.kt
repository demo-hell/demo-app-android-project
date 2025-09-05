package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateConsents<out T> {
    data class Success<T>(val data: T? = null) : UIStateConsents<T>()
    data class Error(val message: String? = null) : UIStateConsents<Nothing>()
    data class ErrorWithoutAccess(val message: String? = null) : UIStateConsents<Nothing>()

    object Loading : UIStateConsents<Nothing>()
}
