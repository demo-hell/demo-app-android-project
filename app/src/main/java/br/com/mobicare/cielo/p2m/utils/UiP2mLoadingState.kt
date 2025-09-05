package br.com.mobicare.cielo.p2m.utils

sealed class UiP2mLoadingState {
    object ShowLoading : UiP2mLoadingState()
    object ShowLoadingSuccess : UiP2mLoadingState()
    object HideLoading : UiP2mLoadingState()
}