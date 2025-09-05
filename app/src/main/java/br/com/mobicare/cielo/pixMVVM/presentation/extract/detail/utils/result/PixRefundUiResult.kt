package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result

sealed class PixRefundUiResult {
    object RefundReceived : PixRefundUiResult()
    abstract class RefundSent : PixRefundUiResult()

    object RefundSentPending : RefundSent()
    object RefundSentFailed : RefundSent()
    object RefundSentCompleted : RefundSent()
}