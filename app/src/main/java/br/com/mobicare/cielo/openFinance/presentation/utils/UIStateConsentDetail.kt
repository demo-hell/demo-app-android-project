package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateConsentDetail<out T> {
    data class Success<T>(val data: T? = null) : UIStateConsentDetail<T>()
    data class Error(val message: String? = null) : UIStateConsentDetail<Nothing>()
    data class ErrorWithoutAccess(val message: String? = null) : UIStateConsentDetail<Nothing>()
    object Loading : UIStateConsentDetail<Nothing>()
}
