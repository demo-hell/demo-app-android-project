package br.com.mobicare.cielo.arv.utils

sealed class UiArvLoadingState {
    object ShowLoading : UiArvLoadingState()
    object HideLoading : UiArvLoadingState()
}