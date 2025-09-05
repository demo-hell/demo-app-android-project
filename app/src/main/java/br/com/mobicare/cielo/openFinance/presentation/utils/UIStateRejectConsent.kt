package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateRejectConsent<out T>{
    data class Success<T>(val data: T? = null) : UIStateRejectConsent<T>()
    data class Error(val message: String? = null): UIStateRejectConsent<Nothing>()
    object Loading : UIStateRejectConsent<Nothing>()
}
