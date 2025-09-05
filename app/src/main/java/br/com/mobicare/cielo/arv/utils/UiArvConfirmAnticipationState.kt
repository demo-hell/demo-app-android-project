package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.data.model.response.ArvConfirmAnticipationResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvConfirmAnticipationState {
    data class Error(val error: NewErrorMessage?) : UiArvConfirmAnticipationState()

    data class ErrorToken(val error: NewErrorMessage?) : UiArvConfirmAnticipationState()

    data class ErrorNotEligible(val error: NewErrorMessage?) : UiArvConfirmAnticipationState()

    data class Success(val response: ArvConfirmAnticipationResponse) : UiArvConfirmAnticipationState()
}
