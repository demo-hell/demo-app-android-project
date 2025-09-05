package br.com.mobicare.cielo.tapOnPhone.presentation.impersonate

import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneBaseView

interface TapOnPhoneImpersonateContract {

    interface View : TapOnPhoneBaseView {
        fun onSuccessImpersonateECTap(impersonate: Impersonate, merchant: Merchant)
    }

    interface Presenter {
        fun findECTap(
            eligibilityResponse: TapOnPhoneEligibilityResponse?,
            fingerprint: String
        )
    }
}