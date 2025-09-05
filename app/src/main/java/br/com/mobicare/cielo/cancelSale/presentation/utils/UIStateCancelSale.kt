package br.com.mobicare.cielo.cancelSale.presentation.utils

sealed class UIStateCancelSale <out T>  {
    data class Success<T>(val data: T? = null) : UIStateCancelSale<T>()
    object ErrorGeneric : UIStateCancelSale<Nothing>()
    data class ErrorEspecify<T>(val data: T? = null) : UIStateCancelSale<T>()
}