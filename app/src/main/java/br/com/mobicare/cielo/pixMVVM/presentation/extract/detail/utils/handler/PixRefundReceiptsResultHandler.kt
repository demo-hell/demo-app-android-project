package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundReceiptsUiResult

class PixRefundReceiptsResultHandler {

    private lateinit var refundReceipts: PixRefundReceipts
    private lateinit var transferDetail: PixTransferDetail

    operator fun invoke(
        refundReceipts: PixRefundReceipts,
        transferDetail: PixTransferDetail
    ): PixRefundReceiptsUiResult {
        this.refundReceipts = refundReceipts
        this.transferDetail = transferDetail

        return if (canBeRefunded) {
            PixRefundReceiptsUiResult.CanBeRefunded(refundReceipts)
        } else {
            PixRefundReceiptsUiResult.CannotBeRefunded
        }
    }

    private val canBeRefunded get() = isReversalNotExpired && hasReversalAmount

    private val isReversalNotExpired get() = transferDetail.expiredReversal?.not() == true

    private val hasReversalAmount get() = refundReceipts.totalAmountPossibleReversal.let {
        it != null && it > ZERO_DOUBLE
    }

}