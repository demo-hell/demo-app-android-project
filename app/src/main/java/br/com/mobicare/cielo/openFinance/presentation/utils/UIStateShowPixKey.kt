package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateShowPixKey<out T>{
    data class HidePixKey<T>(val data: T? = null) : UIStateShowPixKey<T>()
    data class ShowPixKey<T>(val data: T? = null) : UIStateShowPixKey<T>()
}
