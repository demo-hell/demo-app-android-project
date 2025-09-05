package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views

import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.PixReceiptQrCodeChangeTransferReceivedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.PixReceiptQrCodeTransferReceivedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.PixReceiptQrCodeWithdrawalTransferReceivedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.PixReceiptTransferReceivedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptAutomaticTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptFeeTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeChangeTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptQrCodeWithdrawalTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed.PixStatusFeeTransferFailedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed.PixStatusTransferFailedViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending.PixStatusAutomaticTransferPendingViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending.PixStatusFeeTransferPendingViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending.PixStatusTransferPendingViewBuilder

class PixTransferViewSelector(
    private val inflater: LayoutInflater,
    private val data: PixTransferDetail,
    private val onAmountTap: (PixTransferDetail.Credit?) -> Unit,
    private val onNetAmountTap: (PixTransferDetail.Settlement?) -> Unit,
    private val onFeeAmountTap: (PixTransferDetail.Fee?) -> Unit
) {

    operator fun invoke(result: PixTransferUiResult) = when (result) {
        is PixTransferUiResult.TransferSent -> handleTransferSentResult(result)
        is PixTransferUiResult.TransferReceived -> handleTransferReceivedResult(result)
        is PixTransferUiResult.TransferCanceled -> handleTransferCanceledResult(result)
        is PixTransferUiResult.TransferInProcess -> handleTransferInProcessResult(result)
    }

    private fun handleTransferSentResult(
        result: PixTransferUiResult.TransferSent
    ) = when (result) {
        is PixTransferUiResult.FeeTransferSent ->
            PixReceiptFeeTransferSentViewBuilder(
                inflater,
                data,
                onAmountTap = onAmountTap,
                onNetAmountTap = onNetAmountTap
            ).build()
        is PixTransferUiResult.AutomaticTransferSent -> {
            PixReceiptAutomaticTransferSentViewBuilder(inflater, data).build()
        }
        is PixTransferUiResult.QrCodeTransferSent ->
            handleQrCodeTransferSentResult(result)
        else ->
            PixReceiptTransferSentViewBuilder(inflater, data).build()
    }

    private fun handleQrCodeTransferSentResult(
        result: PixTransferUiResult.QrCodeTransferSent
    ) = when (result) {
        is PixTransferUiResult.QrCodeWithdrawalTransferSent ->
            PixReceiptQrCodeWithdrawalTransferSentViewBuilder(inflater, data).build()
        is PixTransferUiResult.QrCodeChangeTransferSent ->
            PixReceiptQrCodeChangeTransferSentViewBuilder(inflater, data).build()
        else ->
            PixReceiptQrCodeTransferSentViewBuilder(inflater, data).build()
    }

    private fun handleTransferReceivedResult(
        result: PixTransferUiResult.TransferReceived
    ) = when (result) {
        is PixTransferUiResult.QrCodeWithdrawalTransferReceived ->
            PixReceiptQrCodeWithdrawalTransferReceivedViewBuilder(
                inflater,
                data
            ).build()
        is PixTransferUiResult.QrCodeChangeTransferReceived ->
            PixReceiptQrCodeChangeTransferReceivedViewBuilder(
                inflater,
                data,
                onFeeTap = onFeeAmountTap,
                onNetAmountTap = onNetAmountTap
            ).build()
        is PixTransferUiResult.QrCodeTransferReceived ->
            PixReceiptQrCodeTransferReceivedViewBuilder(
                inflater,
                data,
                onFeeTap = onFeeAmountTap,
                onNetAmountTap = onNetAmountTap
            ).build()
        else ->
            PixReceiptTransferReceivedViewBuilder(
                inflater,
                data,
                onFeeTap = onFeeAmountTap,
                onNetAmountTap = onNetAmountTap
            ).build()
    }

    private fun handleTransferCanceledResult(
        result: PixTransferUiResult.TransferCanceled
    ) = when (result) {
        is PixTransferUiResult.FeeTransferCanceled ->
            PixStatusFeeTransferFailedViewBuilder(inflater, data).build()
        else ->
            PixStatusTransferFailedViewBuilder(inflater, data).build()
    }

    private fun handleTransferInProcessResult(
        result: PixTransferUiResult.TransferInProcess
    ) = when (result) {
        is PixTransferUiResult.FeeTransferInProcess ->
            PixStatusFeeTransferPendingViewBuilder(inflater, data).build()
        is PixTransferUiResult.AutomaticTransferInProcess ->
            PixStatusAutomaticTransferPendingViewBuilder(
                inflater,
                data,
                onAccessOriginalTransactionTap = onAmountTap
            ).build()
        else ->
            PixStatusTransferPendingViewBuilder(inflater, data).build()
    }

}