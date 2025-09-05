package br.com.mobicare.cielo.pix.ui.extract.receipt

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.SchedulingDetailResponse

interface PixScheduledTransactionReceiptContract {

    interface View : BaseView {
        fun onShowScheduledTransactionReceipt(pixSchedulingDetail: SchedulingDetailResponse)
    }

    interface Presenter {
        fun onGetScheduling(pixSchedulingDetail: SchedulingDetailResponse?, schedulingCode: String?)
        fun onResume()
        fun onPause()
    }
}