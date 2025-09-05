package br.com.mobicare.cielo.tapOnPhone.presentation.router

import android.content.Context
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneBaseView

interface TapOnPhoneContract {

    interface View : TapOnPhoneBaseView {
        fun onNonEligible()
        fun onToDoAccreditation()
        fun onAccreditationInProgress()
        fun onEstablishmentCreationInProgress()
        fun onTapIsActive(wasOpenedByPOSVirtual: Boolean = false)
        fun onShowCallCenter(error: ErrorMessage)
        fun onExchangeEstablishment(eligibilityResponse: TapOnPhoneEligibilityResponse)
        fun onTapIsDisabled()
    }

    interface Presenter {
        fun onGetTapStatus(wasOpenedByPOSVirtual: Boolean)
        fun onPause()
        fun onResume()
        fun onDeleteCache(context: Context)
    }
}