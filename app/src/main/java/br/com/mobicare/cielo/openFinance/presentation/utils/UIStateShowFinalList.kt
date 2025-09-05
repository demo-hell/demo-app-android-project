package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateShowFinalList<out T> {
    data class HideFinalList<T>(val data: T? = null) : UIStateShowFinalList<T>()
    data class ShowFinalList<T>(val data: T? = null) : UIStateShowFinalList<T>()
}