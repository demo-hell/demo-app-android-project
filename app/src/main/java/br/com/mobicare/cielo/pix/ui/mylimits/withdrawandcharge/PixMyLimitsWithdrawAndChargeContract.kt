package br.com.mobicare.cielo.pix.ui.mylimits.withdrawandcharge

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.LimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum

interface PixMyLimitsWithdrawAndChargeContract {

    interface View : BaseView {
        fun onShowMyLimits(limitsResponse: PixMyLimitsResponse)
        fun onErrorUpdateLimits(onGenericError: () -> Unit)
        fun onErrorGetLimits(errorMessage: ErrorMessage? = null)
        fun onSuccessUpdateLimit()
        fun onErrorUpdateLimit(errorMessage: ErrorMessage? = null)
        fun onSuccessGetNightTime(timeManagement: PixTimeManagementEnum?)
    }

    interface Presenter {
        fun getUsername(): String
        fun getMyLimits()
        fun onUpdateLimit(
            otp: String?,
            listLimits: MutableList<LimitsRequest>?,
            fingerprint: String
        )
        fun getNightTime()
        fun onResume()
        fun onPause()
    }
}