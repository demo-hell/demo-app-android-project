package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse

interface TaxasPlanosCieloContract {

    interface Presenter : BasePresenter<View> {
        fun load(planName: String)
        fun onDestroy()
    }

    interface View : BaseView, IAttached {
        fun onError(error: ErrorMessage)
        fun showFreeCieloData(taxaPlanosDetail: TaxaPlanosDetailsResponse)
        fun showControlCieloData(taxaPlanosDetail: TaxaPlanosDetailsResponse)
    }

}