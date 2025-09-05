package br.com.mobicare.cielo.home.presentation.arv.viewmodel

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvCardAlertState {
    object HideArvCardAlert : UiArvCardAlertState()

    data class ShowArvCardAlert(val arvAnticipation: ArvAnticipation) : UiArvCardAlertState()

    data class Error(val error: NewErrorMessage) : UiArvCardAlertState()
}
