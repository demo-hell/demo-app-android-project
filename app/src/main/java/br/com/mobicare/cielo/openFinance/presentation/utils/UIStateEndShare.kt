package br.com.mobicare.cielo.openFinance.presentation.utils

sealed class UIStateEndShare {
    object LoadingEndShare : UIStateEndShare()
    object ErrorEndShare : UIStateEndShare()
    object SuccessEndShare : UIStateEndShare()
    object WithoutAccessEndShare : UIStateEndShare()
}