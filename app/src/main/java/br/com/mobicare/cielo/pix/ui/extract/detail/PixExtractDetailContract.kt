package br.com.mobicare.cielo.pix.ui.extract.detail

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.ReversalReceiptsResponse
import br.com.mobicare.cielo.pix.domain.ScheduleCancelRequest
import br.com.mobicare.cielo.pix.domain.SchedulingDetailResponse
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse

interface PixExtractDetailContract {

    interface View : BaseView {
        fun onQRCodeTransferReceived(details: TransferDetailsResponse)
        fun onQRCodeTransferSent(details: TransferDetailsResponse)
        fun onQRCodeTransferInProcess(details: TransferDetailsResponse)
        fun onQRCodeTransferCancel(details: TransferDetailsResponse)

        fun onQRCodeChangeTransferReceived(details: TransferDetailsResponse)
        fun onQRCodeChangePaymentSent(details: TransferDetailsResponse)
        fun onQRCodeChangePaymentInProcess(details: TransferDetailsResponse)
        fun onQRCodeChangePaymentCancel(details: TransferDetailsResponse)

        fun onQRCodeWithdrawalTransferReceived(details: TransferDetailsResponse)
        fun onQRCodeWithdrawalTransferSent(details: TransferDetailsResponse)
        fun onQRCodeWithdrawalTransferInProcess(details: TransferDetailsResponse)
        fun onQRCodeWithdrawalTransferCancel(details: TransferDetailsResponse)

        fun onTransferSent(details: TransferDetailsResponse)
        fun onTransferReceived(details: TransferDetailsResponse)
        fun onTransferInProcess(details: TransferDetailsResponse)
        fun onTransferCancel(details: TransferDetailsResponse)
        fun onTransactionCancelSuccess(onAction: () -> Unit)
        fun onFeeTransferSent(details: TransferDetailsResponse)
        fun onFeeTransferInProcess(details: TransferDetailsResponse)
        fun onFeeTransferCancel(details: TransferDetailsResponse)
        fun onAutomaticTransferInProcess(details: TransferDetailsResponse)
        fun onAutomaticTransferSent(details: TransferDetailsResponse)
        fun onAutomaticTransferCancel(details: TransferDetailsResponse)

        fun onPixScheduled(scheduledTransaction: SchedulingDetailResponse?)
        fun onProcessError(onFirstAction: () -> Unit)

        fun onReversalCompletedTransaction(
            details: TransferDetailsResponse?,
            wasReceived: Boolean = false
        )

        fun onFailedReversalTransaction(
            details: TransferDetailsResponse?,
            wasReceived: Boolean = false
        )

        fun onPendingReversalTransaction(
            details: TransferDetailsResponse?,
            wasReceived: Boolean = false
        )

        fun setReversalData(details: TransferDetailsResponse)
        fun showReversalReceiptsLoading()
        fun hideReversalReceiptsLoading()
        fun showReversalTryAgainLoading()
        fun hideReversalTryAgainLoading()
        fun setReversalReceipts(receiptsResponse: ReversalReceiptsResponse)

        fun showRefundsHistoryError(transferDetails: TransferDetailsResponse?)
    }

    interface Presenter {
        fun getUsername(): String
        fun getSchedulingCode(): String?
        fun getUpdatedDetailsResponse(): TransferDetailsResponse?
        fun getDetails(
            transactionCode: String?,
            idEndToEnd: String?,
            schedulingCode: String? = null
        )

        fun getReceipts(idEndToEnd: String?, isTryAgain: Boolean = false)
        fun getReversalTransactionDetails(transactionCode: String)
        fun cancelTransactionScheduled(
            schedulingCode: String?,
            otp: String,
            scheduleCancelRequest: ScheduleCancelRequest
        )

        fun onResume()
        fun onPause()
        fun canGoToReversal(isHome: Boolean): Boolean
        fun isShowTransactionAnalyseButton(): Boolean
    }
}