package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult

class PixRefundResultHandler {

    private lateinit var data: PixRefundDetail

    operator fun invoke(data: PixRefundDetail): PixRefundUiResult {
        this.data = data

        return if (isRefundReceived) {
            PixRefundUiResult.RefundReceived
        } else when (data.transactionStatus) {
            PixTransactionStatus.PENDING,
            PixTransactionStatus.PROCESSING -> PixRefundUiResult.RefundSentPending
            PixTransactionStatus.FAILED,
            PixTransactionStatus.NOT_EXECUTED,
            PixTransactionStatus.SENT_WITH_ERROR -> PixRefundUiResult.RefundSentFailed
            else -> PixRefundUiResult.RefundSentCompleted
        }
    }

    private val isRefundReceived get() =
        data.transactionType == PixExtractType.REVERSAL_CREDIT

}