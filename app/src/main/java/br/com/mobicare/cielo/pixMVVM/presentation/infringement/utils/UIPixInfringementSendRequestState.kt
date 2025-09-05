package br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPixInfringementSendRequestState {

    object ShowLoading : UIPixInfringementSendRequestState()

    object HideLoading : UIPixInfringementSendRequestState()

    object Success : UIPixInfringementSendRequestState()

    data class Error(val error: NewErrorMessage? = null) : UIPixInfringementSendRequestState()

}