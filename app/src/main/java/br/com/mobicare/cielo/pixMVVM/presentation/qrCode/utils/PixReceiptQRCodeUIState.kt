package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

sealed class PixReceiptQRCodeUIState {
    object ShowLoading : PixReceiptQRCodeUIState()

    object HideLoading : PixReceiptQRCodeUIState()

    data class TransactionExecutedSuccess(
        val data: PixTransferDetail,
    ) : PixReceiptQRCodeUIState()

    data class TransactionScheduledSuccess(
        val data: PixSchedulingDetail,
    ) : PixReceiptQRCodeUIState()

    object Error : PixReceiptQRCodeUIState()

    object ReturnBackScreen : PixReceiptQRCodeUIState()
}
