package br.com.mobicare.cielo.centralDeAjuda.presentation.ui

import android.app.Activity
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.Manager
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.PhoneSupport
import br.com.mobicare.cielo.commons.ui.IAttached


interface CentralAjudaContract {
    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showContent(ajuda: CentralAjudaObj)
        fun hideContent()
        fun showGestor(manager: Manager)
        fun hideGestor()
        fun loadPhoneSupport(phones: List<PhoneSupport>)
        fun loadHeader(obj: CentralAjudaObj)
        fun showError(error: String)
        fun showForgetNaturalUserAndEstablishiment(title: String)
        fun showForgetLegalUserAndEstablishment(title: String)
    }

    interface Presenter {
        fun callAPI()
        fun onResume()
        fun onPause()
    }
}
