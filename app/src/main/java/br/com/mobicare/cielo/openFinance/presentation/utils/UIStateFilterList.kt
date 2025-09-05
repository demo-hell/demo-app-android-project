package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateFilterList<out T> {
    data class ListFiltered<T>(val data: T? = null) : UIStateFilterList<T>()
    data class NotFound<T>(val data: T? = null) : UIStateFilterList<T>()

}