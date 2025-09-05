package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvBrandsSelectionState {
    object ShowLoadingAnticipation: UiArvBrandsSelectionState()
    object HideLoadingAnticipation: UiArvBrandsSelectionState()
    class ShowError(val error: NewErrorMessage?): UiArvBrandsSelectionState()
    class SuccessLoadArvAnticipation(val anticipation: ArvAnticipation):
        UiArvBrandsSelectionState()
}