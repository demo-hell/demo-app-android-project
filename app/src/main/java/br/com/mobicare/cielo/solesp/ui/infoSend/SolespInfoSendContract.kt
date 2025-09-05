package br.com.mobicare.cielo.solesp.ui.infoSend

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.solesp.model.SolespModel

interface SolespInfoSendContract {

    interface View : BaseView {
        fun showError()
        fun showSuccess()
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun getNumberEc(): String
        fun getUserName(): String
        fun sendSolespRequest(solespModel: SolespModel)
    }

}