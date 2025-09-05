package br.com.mobicare.cielo.pix.ui.extract.reversal.receipt

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.domain.ReversalDetailsFullResponse

interface PixReversalReceiptContract {

    interface View : BaseView {
        fun onShowReversalData(reversalDetailsResponse: ReversalDetailsFullResponse)
    }

    interface Presenter {
        fun onResume()
        fun onPause()
        fun getReversalDetails(
            transactionCode: String?
        )
    }
}