package br.com.mobicare.cielo.pix.ui.mylimits.timemanagement

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.LimitsRequest
import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest
import br.com.mobicare.cielo.pix.domain.PixTimeManagementResponse

interface PixMyLimitsTimeManagementContract {
    interface View : BaseView {
        fun onSuccessGetNightTime(nightTimeResponse: PixTimeManagementResponse)
        fun onErrorGetNightTime(errorMessage: ErrorMessage? = null)
        fun onSuccessUpdateNightTime()
        fun onErrorUpdateNightTime(onGenericError: () -> Unit)
    }

    interface Presenter {
        fun getUsername(): String
        fun getNightTime()

        fun onUpdateNightTime(
            otp: String?,
            nightTimeStart: String?
        )

        fun onResume()
        fun onPause()
    }
}