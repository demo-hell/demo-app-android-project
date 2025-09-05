package br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse

interface PixBillingReceiptContract {

    interface View : BaseView {
        fun onShowReceipt(response: TransferDetailsResponse)
    }

    interface Presenter {
        fun onGetReceipt(transactionCode: String?, idEndToEnd: String?)
        fun onResume()
        fun onPause()
    }
}