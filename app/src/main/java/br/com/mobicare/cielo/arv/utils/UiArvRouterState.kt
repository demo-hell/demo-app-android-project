package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation

sealed class UiArvRouterState {
    object ShowHome : UiArvRouterState()
    object ShowOnboarding : UiArvRouterState()
    object ShowUnavailableService : UiArvRouterState()
    data class ShowArvSingleAnticipation(val arvAnticipation: ArvAnticipation) : UiArvRouterState()
}