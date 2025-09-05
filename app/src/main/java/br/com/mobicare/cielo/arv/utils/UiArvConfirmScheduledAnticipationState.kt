package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvConfirmScheduledAnticipationState {
    data class Error(val error: NewErrorMessage?) : UiArvConfirmScheduledAnticipationState()

    data class ErrorToken(val error: NewErrorMessage?) : UiArvConfirmScheduledAnticipationState()

    data class ErrorNotEligible(val error: NewErrorMessage?) : UiArvConfirmScheduledAnticipationState()

    object Success : UiArvConfirmScheduledAnticipationState()

    object ShowLoading : UiArvConfirmScheduledAnticipationState()

    object HideLoading : UiArvConfirmScheduledAnticipationState()
}
