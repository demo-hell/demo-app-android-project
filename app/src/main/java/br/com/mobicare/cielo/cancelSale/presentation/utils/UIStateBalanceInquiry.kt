package br.com.mobicare.cielo.cancelSale.presentation.utils

sealed class UIStateBalanceInquiry <out T>  {
    object Loading : UIStateBalanceInquiry<Nothing>()
    data class Success<T>(val data: T? = null) : UIStateBalanceInquiry<T>()
    object Error : UIStateBalanceInquiry<Nothing>()
    object ErrorSaleHasBeenCancelled : UIStateBalanceInquiry<Nothing>()
}