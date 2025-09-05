package br.com.mobicare.cielo.pix.ui.transfer.summary

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.*

interface PixTransferSummaryContract {

    interface View : BaseView {
        fun onSuccessFlow(action: () -> Unit)
        fun showBottomSheetSuccessfulTransaction(
            details: TransferDetailsResponse,
            transferResponse: PixTransferResponse
        )
        fun showBottomSheetScheduledTransaction(
            scheduledTransactionInfo: PixExtractResponse
        )
        fun onTransactionInProcess()
        fun onError(message: String?)
        fun onPixManyRequestsError(error: ErrorMessage?)
    }

    interface Presenter {
        fun getUsername(): String
        fun onTransfer(
            otp: String,
            response: ValidateKeyResponse?,
            amount: Double,
            message: String?,
            fingerprintAllowme: String,
            chosenScheduleDate: String?
        )
        fun onBankTransfer(otp: String, request: PixManualTransferRequest?)
        fun onResume()
        fun onPause()
    }
}