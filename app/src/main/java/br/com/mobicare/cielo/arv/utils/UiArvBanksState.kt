package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvBanksState {
    object ShowLoadingArvBanks : UiArvBanksState()
    class ShowTryAgain(val error: NewErrorMessage?) : UiArvBanksState()
    class SuccessArvBanks(val banks: List<ArvBank>) : UiArvBanksState()
}