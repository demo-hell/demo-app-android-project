package br.com.mobicare.cielo.conciliador.onboarding

import br.com.mobicare.cielo.commons.presentation.BaseView

interface CieloFacilitaOnboardingContract {

    interface View : BaseView {
        fun onFinish()
        fun onStartOnBoarding()
        fun onStartAcceptTerms()
        fun onIneligible()
        fun onStartSales()
        fun onStartAcquirer()
        fun onShowServiceUnavailabilityMessage(message: String?)
    }

    interface Presenter : BaseView {
        fun getEligible()
        fun onResume()
        fun onDestroy()
    }
}