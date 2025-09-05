package br.com.mobicare.cielo.pix.ui.extract.reversal

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.ReversalDetailsResponse

interface PixReversalContract {

    interface View : BaseView {
        fun onShowSuccessReversal(details: ReversalDetailsResponse)
        fun onTransactionInProcess()
        fun onError()
    }

    interface Presenter {
        fun getUsername(): String
        fun onReverse(
            otp: String,
            idEndToEnd: String?,
            amount: Double,
            message: String?,
            fingerprintAllowme: String,
            idTx: String?
        )

        fun onResume()
        fun onPause()
    }
}