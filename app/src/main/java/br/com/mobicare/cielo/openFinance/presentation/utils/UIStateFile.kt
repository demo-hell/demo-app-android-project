package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateFile<out T> {
    object LoadingDocument : UIStateFile<Nothing>()
    data class SuccessDocument<T>(val data: T? = null) : UIStateFile<T>()
    object ErrorDocument : UIStateFile<Nothing>()
}