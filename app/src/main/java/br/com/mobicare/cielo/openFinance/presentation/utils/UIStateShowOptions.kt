package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateShowOptions<out T> {
    data class HideOptions<T>(val data: T? = null) : UIStateShowOptions<T>()
    data class ShowOptions<T>(val data: T? = null) : UIStateShowOptions<T>()
}
