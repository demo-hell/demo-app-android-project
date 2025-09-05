package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.presentation.model.ScheduleContract
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvBranchesContractsState {
    object ShowLoading : UiArvBranchesContractsState()
    object HideLoading : UiArvBranchesContractsState()
    class ShowError(val error: NewErrorMessage) : UiArvBranchesContractsState()
    class SuccessListContracts(val contracts: List<ScheduleContract>) : UiArvBranchesContractsState()
    object AlreadyShowed : UiArvBranchesContractsState()
}