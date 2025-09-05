package br.com.mobicare.cielo.p2m.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiP2mAcceptState {
    object Success : UiP2mAcceptState()
    object Empty : UiP2mAcceptState()
    class Error(val message: NewErrorMessage? = null) : UiP2mAcceptState()
    object ShowLoading : UiP2mAcceptState()
    object HideLoading : UiP2mAcceptState()
}

sealed class UiTaxTextState<out T> {
    open class Success<T>(val data: T? = null) : UiTaxTextState<T>()
    class Error(val message: String? = null): UiTaxTextState<Nothing>()
    object Loading : UiTaxTextState<Nothing>()
    object HideLoading : UiTaxTextState<Nothing>()
    object Empty : UiTaxTextState<Nothing>()
}