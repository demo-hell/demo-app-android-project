package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult

sealed class PixPaymentQRCodeUIState {
    object HideLoading : PixPaymentQRCodeUIState()

    data class TransactionExecuted(
        val result: PixTransferResult,
        val typeQRCode: PixQrCodeOperationType,
    ) : PixPaymentQRCodeUIState()

    data class TransactionScheduled(
        val result: PixTransferResult,
    ) : PixPaymentQRCodeUIState()

    object TransactionProcessing : PixPaymentQRCodeUIState()

    object TransactionFailed : PixPaymentQRCodeUIState()

    data class FourHundredError(
        val error: NewErrorMessage? = null,
    ) : PixPaymentQRCodeUIState()

    data class GenericError(
        val error: NewErrorMessage? = null,
    ) : PixPaymentQRCodeUIState()

    data class TokenError(
        val error: NewErrorMessage? = null,
    ) : PixPaymentQRCodeUIState()

    object DoNothing : PixPaymentQRCodeUIState()
}
