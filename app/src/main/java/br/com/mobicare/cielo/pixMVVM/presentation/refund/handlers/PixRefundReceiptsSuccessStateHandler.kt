package br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.orZero
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState

class PixRefundReceiptsSuccessStateHandler {

    private lateinit var refundReceipts: PixRefundReceipts
    private var isExpiredReversal: Boolean = false

    operator fun invoke(
        refundReceipts: PixRefundReceipts,
        isExpiredReversal: Boolean?
    ): PixRefundReceiptsUiState.Success {
        this.refundReceipts = refundReceipts
        isExpiredReversal?.let { this.isExpiredReversal = it }

        return when {
            isFullyRefunded -> PixRefundReceiptsUiState.FullyRefunded
            isPartiallyRefundedAndNotExpired -> PixRefundReceiptsUiState.PartiallyRefunded
            isPartiallyRefundedAndExpired -> PixRefundReceiptsUiState.PartiallyRefundedButExpired
            isNotRefundedAndNotExpired -> PixRefundReceiptsUiState.NotRefunded
            isNotRefundedAndExpired -> PixRefundReceiptsUiState.NotRefundedButExpired
            else -> PixRefundReceiptsUiState.Unknown
        }
    }

    private val isFullyRefunded get() = refundReceipts.run {
        receipts.isNullOrEmpty().not() && totalAmountPossibleReversal.orZero() == ZERO_DOUBLE
    }

    private val isNotRefundedAndNotExpired get() = refundReceipts.run {
        receipts.isNullOrEmpty()
                && totalAmountPossibleReversal.orZero() > ZERO_DOUBLE
                && isExpiredReversal.not()
    }

    private val isNotRefundedAndExpired get() = refundReceipts.run {
        receipts.isNullOrEmpty() && isExpiredReversal
    }

    private val isPartiallyRefunded get() = refundReceipts.run {
        receipts.isNullOrEmpty().not() && totalAmountPossibleReversal.orZero() > ZERO_DOUBLE
    }

    private val isPartiallyRefundedAndNotExpired get() = refundReceipts.run {
        isPartiallyRefunded && isExpiredReversal.not()
    }

    private val isPartiallyRefundedAndExpired get() = refundReceipts.run {
        isPartiallyRefunded && isExpiredReversal
    }

}