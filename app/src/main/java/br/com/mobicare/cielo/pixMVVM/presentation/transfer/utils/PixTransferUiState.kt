package br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class PixTransferUiState {
    abstract class Success : PixTransferUiState()

    object TransferSent : Success()

    object TransferScheduled : Success()

    abstract class Error : PixTransferUiState()

    data class TokenError(
        val error: NewErrorMessage,
    ) : Error()

    data class GenericError(
        val error: NewErrorMessage? = null,
    ) : Error()

    data class TooManyRequestsError(
        val error: NewErrorMessage,
    ) : Error()

    object DoNothing : PixTransferUiState()
}
