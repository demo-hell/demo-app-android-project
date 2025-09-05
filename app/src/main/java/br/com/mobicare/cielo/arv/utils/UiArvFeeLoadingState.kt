package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvFeeLoadingState {
    object ShowLoading : UiArvFeeLoadingState()
    object HideLoading : UiArvFeeLoadingState()
    data class Error(val error: NewErrorMessage?, val onErrorAction: (() -> Unit)? = null) :
        UiArvFeeLoadingState()
}