package br.com.mobicare.cielo.pix.ui.transfer.receipt

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.domain.PixTransferResponse

interface PixTransferReceiptContract {

    interface View : BaseView {
        fun onShowCommonTransfer(response: TransferDetailsResponse)
        fun onShowManualTransferReceipt(response: TransferDetailsResponse)
        fun onShowQrCodePaymentReceipt(response: TransferDetailsResponse)
        fun onShowWithdrawReceipt(response: TransferDetailsResponse)
        fun onShowChangeReceipt(response: TransferDetailsResponse)
    }

    interface Presenter {
        fun onValidateDetails(
            transactionCode: String?,
            idEndToEnd: String?,
            detailsTransfer: TransferDetailsResponse?
        )
        fun onGetDetails(transactionCode: String?, idEndToEnd: String?, isShowLoading: Boolean = true)
        fun onResume()
        fun onPause()
    }
}