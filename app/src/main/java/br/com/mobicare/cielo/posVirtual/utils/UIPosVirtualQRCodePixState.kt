package br.com.mobicare.cielo.posVirtual.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse

sealed class UIPosVirtualQRCodePixState {

    class Success(val response: PosVirtualCreateQRCodeResponse) : UIPosVirtualQRCodePixState()

    abstract class Error(val error: NewErrorMessage?) : UIPosVirtualQRCodePixState()

    class GenericError(error: NewErrorMessage?) : Error(error)

    class TimeOutError(error: NewErrorMessage?) : Error(error)

    class InvalidAmountError(error: NewErrorMessage?) : Error(error)

    class IntegrationError(error: NewErrorMessage?) : Error(error)

    class LimitExceededError(error: NewErrorMessage?) : Error(error)

    class TokenError(error: NewErrorMessage?) : Error(error)

}