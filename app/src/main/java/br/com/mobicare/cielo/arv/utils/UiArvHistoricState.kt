package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvHistoricState {

    object ShowLoadingHistoric : UiArvHistoricState()
    object HideLoadingHistoric : UiArvHistoricState()
    object ShowLoadingMoreHistoric : UiArvHistoricState()
    object HideLoadingMoreHistoric : UiArvHistoricState()
    object EmptyHistoric : UiArvHistoricState()
    object Success : UiArvHistoricState()
    data class ErrorHistoric(val message: Any?, val error: NewErrorMessage? = null) : UiArvHistoricState()
    data class SuccessHistoric(val historic: Item) : UiArvHistoricState()

}