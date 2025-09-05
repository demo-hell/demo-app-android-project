package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views

import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.PixReceiptRefundReceivedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptRefundSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed.PixStatusRefundFailedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending.PixStatusRefundPendingViewBuilder

class PixRefundViewSelector(
    private val inflater: LayoutInflater,
    private val data: PixRefundDetailFull
) {

    operator fun invoke(result: PixRefundUiResult) = when (result) {
        is PixRefundUiResult.RefundReceived -> {
            PixReceiptRefundReceivedViewBuilder(inflater, data).build()
        }
        is PixRefundUiResult.RefundSent -> handleRefundSentResult(result)
    }

    private fun handleRefundSentResult(result: PixRefundUiResult.RefundSent) = when (result) {
        is PixRefundUiResult.RefundSentPending ->
            PixStatusRefundPendingViewBuilder(inflater, data).build()
        is PixRefundUiResult.RefundSentCompleted ->
            PixReceiptRefundSentViewBuilder(inflater, data).build()
        else ->
            PixStatusRefundFailedViewBuilder(inflater, data).build()
    }

}