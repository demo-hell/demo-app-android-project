package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateConclusionShare<out T>  {
    object Loading : UIStateConclusionShare<Nothing>()
    data class SuccessShare<T>(val data: T? = null) : UIStateConclusionShare<T>()
    object ErrorShare : UIStateConclusionShare<Nothing>()
}