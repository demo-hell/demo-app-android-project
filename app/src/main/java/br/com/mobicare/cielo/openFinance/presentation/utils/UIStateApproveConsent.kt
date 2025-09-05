package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateApproveConsent<out T>{
    data class Success<T>(val data: T? = null) : UIStateApproveConsent<T>()
    data class ErrorPaymentInProgress<T>(val data: T? = null) : UIStateApproveConsent<T>()
    object Loading : UIStateApproveConsent<Nothing>()
}
