package br.com.mobicare.cielo.chargeback.utils

sealed class UiLoadingState {
    object ShowLoading : UiLoadingState()
    object HideLoading : UiLoadingState()
}