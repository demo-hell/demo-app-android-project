package br.com.mobicare.cielo.superlink.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiSuperLinkState {
    object Loading : UiSuperLinkState()
    object Success : UiSuperLinkState()
    object ErrorNotEligible : UiSuperLinkState()
    class Error(val error: NewErrorMessage? = null) : UiSuperLinkState()
}