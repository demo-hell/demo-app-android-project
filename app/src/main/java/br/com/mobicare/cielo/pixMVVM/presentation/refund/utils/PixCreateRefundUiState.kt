package br.com.mobicare.cielo.pixMVVM.presentation.refund.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class PixCreateRefundUiState {
    object Success : PixCreateRefundUiState()

    abstract class Error : PixCreateRefundUiState()
    data class TokenError(val error: NewErrorMessage) : Error()
    data class GenericError(val error: NewErrorMessage? = null) : Error()
    data class Unprocessable(val error: NewErrorMessage) : Error()
}
