package br.com.mobicare.cielo.commons.utils

sealed class UILoadingState {

    object ShowLoading : UILoadingState()

    object HideLoading : UILoadingState()

}