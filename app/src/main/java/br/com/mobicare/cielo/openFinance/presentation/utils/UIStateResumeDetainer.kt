package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateResumeDetainer<out T> {
    data class Success<T>(val data: T? = null) : UIStateResumeDetainer<T>()
    data class InvalidPaymentAlreadyAuthorized<T>(val data: T? = null) : UIStateResumeDetainer<T>()
    data class PaymentRequestRejected<T>(val data: T? = null) : UIStateResumeDetainer<T>()
    data class PaymentTimeOver<T>(val data: T? = null) : UIStateResumeDetainer<T>()
    data class Error(val message: String? = null): UIStateResumeDetainer<Nothing>()
    data class WithoutAccess(val message: String? = null): UIStateResumeDetainer<Nothing>()
    object Loading : UIStateResumeDetainer<Nothing>()
}
