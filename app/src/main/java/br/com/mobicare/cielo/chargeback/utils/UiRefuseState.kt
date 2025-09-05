package br.com.mobicare.cielo.chargeback.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiRefuseState {
    object Success : UiRefuseState()
    class Error(val error: NewErrorMessage?) : UiRefuseState()
    class ErrorToken(val error: NewErrorMessage?) : UiRefuseState()

    object FileExtensionIsAccepted : UiRefuseState()
    object FileExtensionIsNotAccepted : UiRefuseState()
}