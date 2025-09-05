package br.com.mobicare.cielo.mdr.ui.state

import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiMdrConfirmationState {
    object ShowLoading : UiMdrConfirmationState()

    object HideLoading : UiMdrConfirmationState()

    object AcceptSuccess : UiMdrConfirmationState()

    object RejectSuccess : UiMdrConfirmationState()

    data class Error(
        val error: NewErrorMessage? = null,
        val isAccepted: Boolean = false,
        val screenName: String = EMPTY_STRING,
    ) : UiMdrConfirmationState()
}
