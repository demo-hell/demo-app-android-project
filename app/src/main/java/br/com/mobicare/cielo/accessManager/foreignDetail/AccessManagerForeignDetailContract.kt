package br.com.mobicare.cielo.accessManager.foreignDetail

import br.com.mobicare.cielo.accessManager.model.AccessManagerForeignUserDetailResponse
import br.com.mobicare.cielo.commons.presentation.BaseView

class AccessManagerForeignDetailContract {
    interface View : BaseView {
        fun getDetailSuccess(userDetail: AccessManagerForeignUserDetailResponse)
        fun showGenericError()
        fun decisionSuccess(decision: String)
        fun decisionError()
        fun onErrorOTP()
    }

    interface Presenter {
        fun getForeignUserDetail(userId: String)
        fun sendForeignUserDecision(
            userId: String,
            decision: String,
            otp: String
        )
        fun getUsername(): String
        fun onPause()
        fun onResume()
    }
}