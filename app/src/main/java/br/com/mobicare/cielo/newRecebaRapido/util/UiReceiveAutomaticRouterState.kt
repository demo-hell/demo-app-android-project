package br.com.mobicare.cielo.newRecebaRapido.util

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.FastRepayRule

sealed class UiReceiveAutomaticRouterState {
    object ShowHome : UiReceiveAutomaticRouterState()

    object ShowOnBoarding : UiReceiveAutomaticRouterState()

    object ShowLoading : UiReceiveAutomaticRouterState()

    object HideLoading : UiReceiveAutomaticRouterState()

    data class ShowIneligibleError(val fastRepayRule: FastRepayRule?) : UiReceiveAutomaticRouterState()

    data class ShowContractedServiceError(val fastRepayRule: FastRepayRule?) : UiReceiveAutomaticRouterState()

    data class ShowGenericError(val error: NewErrorMessage? = null) : UiReceiveAutomaticRouterState()
}
