package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPosVirtualAccreditationState {

    object ShowLoading : UIPosVirtualAccreditationState()

    object HideLoading : UIPosVirtualAccreditationState()

    object Success : UIPosVirtualAccreditationState()

    abstract class Error(val error: NewErrorMessage?) : UIPosVirtualAccreditationState()

    class AntiFraudError(error: NewErrorMessage? = null) : Error(error)

    class GenericError(error: NewErrorMessage? = null) : Error(error)

    class SuspectError(error: NewErrorMessage? = null) : Error(error)

    class UnavailableError(error: NewErrorMessage? = null) : Error(error)

    class RequiredDataFieldError(error: NewErrorMessage? = null) : Error(error)

}