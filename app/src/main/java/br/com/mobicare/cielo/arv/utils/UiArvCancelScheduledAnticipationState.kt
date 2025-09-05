package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvCancelScheduledAnticipationState {
    data class Error(val error: NewErrorMessage?) : UiArvCancelScheduledAnticipationState()
    data class ErrorToken(val error: NewErrorMessage?) : UiArvCancelScheduledAnticipationState()
    object Success : UiArvCancelScheduledAnticipationState()
    object ShowLoading : UiArvCancelScheduledAnticipationState()
    object HideLoading : UiArvCancelScheduledAnticipationState()
}