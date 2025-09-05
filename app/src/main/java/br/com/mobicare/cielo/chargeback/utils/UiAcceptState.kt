package br.com.mobicare.cielo.chargeback.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiAcceptState {
    object Success : UiAcceptState()
    class Error(val error: NewErrorMessage?) : UiAcceptState()
    class ErrorToken(val error: NewErrorMessage?) : UiAcceptState()
}