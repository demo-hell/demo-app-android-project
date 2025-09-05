package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateMerchantList<out T> {
    data class Success<T>(val data: T? = null) : UIStateMerchantList<T>()
    data class Error(val message: String? = null): UIStateMerchantList<Nothing>()
    data class NotFound(val message: String? = null): UIStateMerchantList<Nothing>()
    object Loading : UIStateMerchantList<Nothing>()
}