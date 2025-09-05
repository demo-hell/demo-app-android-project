package br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPixInfringementSelectReasonState {

    object ShowLoading : UIPixInfringementSelectReasonState()

    object HideLoading : UIPixInfringementSelectReasonState()

    object Success : UIPixInfringementSelectReasonState()

    object NavigateToDetailWhatHappened : UIPixInfringementSelectReasonState()

    data class Ineligible(val details: String) : UIPixInfringementSelectReasonState()

    data class Error(val error: NewErrorMessage? = null) : UIPixInfringementSelectReasonState()

}