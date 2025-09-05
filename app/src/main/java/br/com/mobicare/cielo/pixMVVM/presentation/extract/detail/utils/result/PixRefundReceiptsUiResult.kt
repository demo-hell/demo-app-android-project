package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result

import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts

sealed class PixRefundReceiptsUiResult {
    object CannotBeRefunded : PixRefundReceiptsUiResult()
    data class CanBeRefunded(val refundReceipts: PixRefundReceipts) : PixRefundReceiptsUiResult()
}