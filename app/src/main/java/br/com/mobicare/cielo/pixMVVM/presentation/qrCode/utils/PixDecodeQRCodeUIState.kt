package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode

sealed class PixDecodeQRCodeUIState {
    object ShowLoading : PixDecodeQRCodeUIState()

    object HideLoading : PixDecodeQRCodeUIState()

    data class NavigateToPixQRCodePaymentSummary(
        val pixDecodeQRCode: PixDecodeQRCode,
    ) : PixDecodeQRCodeUIState()

    data class NavigateToPixQRCodePaymentInsertAmount(
        val pixDecodeQRCode: PixDecodeQRCode,
        val isPixTypeChange: Boolean,
    ) : PixDecodeQRCodeUIState()

    data class GenericError(
        val error: NewErrorMessage? = null,
    ) : PixDecodeQRCodeUIState()

    object CloseActivity : PixDecodeQRCodeUIState()

    object DoNothing : PixDecodeQRCodeUIState()
}
