package br.com.mobicare.cielo.pix.ui.mylimits.transactions

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.LimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import br.com.mobicare.cielo.pix.enums.BeneficiaryTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTimeManagementEnum

interface PixMyLimitsTransactionsContract {

    interface View: BaseView {
        fun onShowMyLimits(limitsResponse: PixMyLimitsResponse)
        fun onErrorUpdateLimits(onGenericError: () -> Unit)
        fun onErrorGetLimits(errorMessage: ErrorMessage? = null)
        fun onSuccessUpdateLimit()
        fun onErrorUpdateLimit(errorMessage: ErrorMessage? = null)
        fun onSuccessGetNightTime(timeManagement: PixTimeManagementEnum?)
    }

    interface Presenter {
        fun getUsername(): String
        fun getMyLimits(beneficiaryType: BeneficiaryTypeEnum)
        fun onUpdateLimit(
                otp: String?,
                listLimits: MutableList<LimitsRequest>?,
                fingerprint: String,
                beneficiaryType: BeneficiaryTypeEnum
        )
        fun getNightTime()
        fun onResume()
        fun onPause()
    }

}