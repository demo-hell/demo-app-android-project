package br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView

interface PlanFreeTaxesPresenterContract {

    interface View : BaseView {
        fun loadBrands(cardBrands: ArrayList<BandeiraModelView>)
    }

    interface Presenter : CommonPresenter {

        fun fetchAllSupportedBrands()

    }

}