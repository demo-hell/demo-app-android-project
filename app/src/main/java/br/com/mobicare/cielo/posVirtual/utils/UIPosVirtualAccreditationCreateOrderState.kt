package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPosVirtualAccreditationCreateOrderState {

    object HideLoading : UIPosVirtualAccreditationCreateOrderState()

    class Success(val orderID: String) : UIPosVirtualAccreditationCreateOrderState()

    object OpenRequiredDataField : UIPosVirtualAccreditationCreateOrderState()

    object GenerateOTPCode : UIPosVirtualAccreditationCreateOrderState()

    class GenericError(val error: NewErrorMessage? = null) : UIPosVirtualAccreditationCreateOrderState()

    class InvalidBankError(val error: NewErrorMessage? = null) :
        UIPosVirtualAccreditationCreateOrderState()

    class TokenError(val error: NewErrorMessage?) : UIPosVirtualAccreditationCreateOrderState()

}