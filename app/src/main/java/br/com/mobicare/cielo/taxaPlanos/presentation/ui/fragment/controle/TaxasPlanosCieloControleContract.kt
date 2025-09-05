package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.controle

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosDetailsResponse

interface TaxasPlanosCieloControleContract {

    interface Presenter : BasePresenter<View> {
        fun load()
        fun onDestroy()
    }

    interface View: BaseView, IAttached {
        fun onLogout()
        fun onError(error: ErrorMessage)
        fun showData(taxaPlanosDetail: TaxaPlanosDetailsResponse)
    }

}